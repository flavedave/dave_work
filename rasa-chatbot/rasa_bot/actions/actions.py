# This files contains your custom actions which can be used to run
# custom Python code.
#
# See this guide on how to implement these action:
# https://rasa.com/docs/rasa/custom-actions
from typing import Any, Dict, List, Text

import logging
import re
from datetime import date

import requests
import yaml
from rasa_sdk import Action, FormValidationAction, Tracker, ValidationAction
from rasa_sdk.events import (
    ActionExecuted,
    FollowupAction,
    SessionStarted,
    SlotSet,
)
from rasa_sdk.executor import CollectingDispatcher
from rasa_sdk.types import DomainDict

logger = logging.getLogger(__name__)

with open("./endpoints.docker.yml", "r") as f:
    endpoint_config = yaml.safe_load(f)


MESSAGE_FALLBACK = "Entschuldigung, ich konnte Ihre Anfrage nicht verarbeiten. Bitte versuchen Sie es erneut."


def request_llm_faq(user_message: str) -> str:
    """Forward the user input to the LLM-FAQ RAC service and return the response if successful.

    Args:
        user_message (str): Input message from the user.

    Returns:
        str: LLM response or fallback message.
    """
    config = endpoint_config.get("faq_llm_endpoint")
    if not config.get("enabled") or not config.get("url"):
        return f"{MESSAGE_FALLBACK} (nE)"

    request_url = config.get("url") + "/ask"
    request_payload = {"question": user_message}
    logger.info(f"LLM request: url={request_url}, payload={request_payload}")

    try:
        # Todo: https://stackoverflow.com/questions/62599036/python-requests-is-slow-and-takes-very-long-to-complete-http-or-https-request and https://requests.readthedocs.io/en/latest/user/advanced/
        response = requests.post(request_url, json=request_payload)
        response.raise_for_status()  # raise error if unsuccessful request
        response_data = response.json().get("response")
        response_msg = response_data.get(
            "generation", f"{MESSAGE_FALLBACK} (Error: LResF1)"
        )
        if "out-of-scope" in str(response.json()):
            return "Basierend auf den hinterlegten Informationen und Dokumenten kann ich Ihnen leider nicht weiterhelfen. Formulieren Sie Ihre Anfrage bitte anders oder kontaktieren Sie unseren Kundenservice."
        response_src = [
            str(doc.get("metadata").get("source"))
            for doc in response_data.get("documents", [])
        ]
        #if response_data.get("web_search", False):
         #   response_src.append("web_search")
            
        if len(response_src) > 0:
            response_msg = f"{response_msg} (Quellen: {', '.join(response_src)})"
        return response_msg

    except requests.RequestException as e:
        logger.error(f"LLM request failed: {e} - {response}")
        return f"{MESSAGE_FALLBACK} (Error: LResF2)"


class ActionLLMResponse(Action):
    def name(self) -> Text:
        return "action_llm_response"

    async def run(
        self, dispatcher, tracker: Tracker, domain: Dict[Text, Any]
    ) -> List[Dict[Text, Any]]:
        message = tracker.latest_message.get("text")
        # get prevoius bot message
        reversed_events = list(reversed(tracker.events))
        previus_bot_message = None
        for event in reversed_events:
            if event.get("event") == "bot" and event.get("text"):
                previus_bot_message = event.get("text")
                break
        promt_message = (
            f"Context: {previus_bot_message}\n"
            f"User: {message}\n"
        )
        llm_response = request_llm_faq(promt_message)
        dispatcher.utter_message(text=llm_response)
        # dispatcher
        return []


class ValidatePredefinedSlots(ValidationAction):
    @staticmethod
    def validate_insurance_type(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        valid_insurance_types = domain["slots"]["insurance_type"]["values"]
        print(tracker.get_slot("insurance_type"))
        if tracker.get_slot("insurance_type") in valid_insurance_types:
            print("validation succeeded")
            return {"insurance_type": tracker.get_slot("insurance_type")}
        else:
            # validation failed, set this slot to None
            print("validation failed")
            return {"insurance_type": None}

    @staticmethod
    def validate_slot_to_change(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        slot_to_change = tracker.get_slot("slot_to_change")
        active_loop = tracker.active_loop.get("name")
        valid_slots_to_change = domain["slots"]["slot_to_change"]["values"]
        print("slot_to_change is " + slot_to_change)
        print("active_loop is " + active_loop)

        if slot_to_change in valid_slots_to_change:
            valid_slots_to_change_dict = {}
            if active_loop == "phpv_form":
                valid_slots_to_change_dict = {
                    "Versicherungsnehmer": "phpv_beneficiary",
                    "Beruf": "phpv_employment_type",
                    "Schadenfreiheit": "phpv_had_insurance_claims",
                    "Selbstbeteiligung": "phpv_with_contribution",
                }
                # if slot_to_change in valid_slots_to_change_dict.keys():
                #     if tracker.get_slot(valid_slots_to_change_dict[slot_to_change]) is not None:
                #         print("validation succeeded")
                #         return {'slot_to_change': slot_to_change}

            elif active_loop == "hrv_form":
                valid_slots_to_change_dict = {
                    "Gebäudetyp": "hrv_coverage",
                    "Wohnfläche": "hrv_living_area",
                    "Straße": "hrv_street",
                    "Geburtsdatum": "hrv_birth_date",
                    "Schadenfreiheit": "hrv_damage_free",
                    "Beruf": "hrv_public_service",
                    "Versicherungsbeginn": "hrv_insurance_start_date",
                    "Glas Zusatzversicherung": "hrv_glass_surface",
                    "Fahrrad Zusatzversicherung": "hrv_bicycle_insurance",
                    "Haus und Wohnungsschutzbrief": "hrv_emergency_assistance",
                }

            if slot_to_change in valid_slots_to_change_dict.keys():
                if (
                    tracker.get_slot(valid_slots_to_change_dict[slot_to_change])
                    is not None
                ):
                    print("validation succeeded")
                    return {"slot_to_change": slot_to_change}

                # validation failed, set this slot to None
                print("validation failed")
                return {"slot_to_change": None}

        elif slot_to_change is not None:
            print("validation failed")
            return {"slot_to_change": None}

        return []


class ValidatePhpvForm(FormValidationAction):
    def name(self) -> Text:
        return "validate_phpv_form"

    @staticmethod
    def validate_phpv_beneficiary(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        valid_beneficiary_types = domain["slots"]["phpv_beneficiary"]["values"]
        print(slot_value)
        if slot_value in valid_beneficiary_types:
            print("validation succeeded")
            return {"phpv_beneficiary": slot_value}
        else:
            # validation failed, set this slot to None
            print("validation failed")
            return {"phpv_beneficiary": None}

    @staticmethod
    def validate_phpv_employment_type(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        valid_employment_types = domain["slots"]["phpv_employment_type"]["values"]
        print(slot_value)
        if slot_value in valid_employment_types:
            print("validation succeeded")
            return {"phpv_employment_type": slot_value}
        else:
            # validation failed, set this slot to None
            print("validation failed")
            return {"phpv_employment_type": None}


class ValidateHrvForm(FormValidationAction):
    def name(self) -> Text:
        return "validate_hrv_form"

    @staticmethod
    def validate_hrv_street(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        print(slot_value)
        print("no validation necessary")
        return {"hrv_street": slot_value}

    @staticmethod
    def validate_hrv_public_service(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        print(slot_value)
        print("no validation necessary")
        return {"hrv_public_service": slot_value}

    @staticmethod
    def validate_hrv_living_area(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        print(slot_value)
        print("no validation necessary")
        return {"hrv_living_area": slot_value}

    @staticmethod
    def validate_hrv_damage_free(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        print(slot_value)
        print("no validation necessary")
        return {"hrv_damage_free": slot_value}

    @staticmethod
    def validate_hrv_insurance_start_date(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:

        date_pattern = r"\b(\d{2})\.(\d{2})\.(\d{4})\b"
        extracted_date_str = slot_value

        # Search for the date in the string
        match = re.search(date_pattern, extracted_date_str)
        if match:
            print(f"Extracted date: {match.group()}")
            day = int(match.group(1))
            month = int(match.group(2))
            year = int(match.group(3))
            extracted_date = date(year, month, day)
            current_date = date.today()

            if current_date < extracted_date:
                print("validation successful")
                return {"hrv_insurance_start_date": slot_value}
            else:
                print("validation failed")
                return {"hrv_insurance_start_date": None}
        else:
            print("Date format can't be validated")
            return {"hrv_insurance_start_date": slot_value}

    @staticmethod
    def validate_hrv_birth_date(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        print(slot_value)

        date_pattern = r"\b(\d{2})\.(\d{2})\.(\d{4})\b"
        birth_date_str = slot_value
        match = re.search(date_pattern, birth_date_str)

        if match:
            day = int(match.group(1))
            month = int(match.group(2))
            year = int(match.group(3))

            birth_date = date(year, month, day)
            current_date = date.today()

            # Calculate the difference in years
            age_years = current_date.year - birth_date.year

            # Adjust for cases where the current date hasn't reached the birth date in the current year
            if (current_date.month, current_date.day) < (
                birth_date.month,
                birth_date.day,
            ):
                age_years -= 1

            # Check if the age is at least 18
            if age_years >= 18:
                print("validation successful")
                return {"hrv_birth_date": slot_value}
            else:
                print("validation failed")
                return {"hrv_birth_date": None}
        else:
            print("Date format can't be validated")
            return {"hrv_birth_date": slot_value}

    @staticmethod
    def validate_hrv_glass_surface(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        print(slot_value)
        print("no validation necessary")
        return {"hrv_glass_surface": slot_value}

    @staticmethod
    def validate_hrv_coverage(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        valid_building_types = domain["slots"]["hrv_coverage"]["values"]
        print(slot_value)
        if slot_value in valid_building_types:
            print("validation succeeded")
            return {"hrv_coverage": slot_value}
        else:
            # validation failed, set this slot to None
            print("validation failed")
            return {"hrv_coverage": None}

    @staticmethod
    def validate_hrv_bicycle_insurance(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        print(slot_value)
        print("no validation necessary")
        return {"hrv_bicycle_insurance": slot_value}

    @staticmethod
    def validate_hrv_emergency_assistance(
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        print(slot_value)
        print("no validation necessary")
        return {"hrv_emergency_assistance": slot_value}


class ValidateKfzForm(FormValidationAction):
    def name(self) -> Text:
        return "validate_kfz_form"

    @staticmethod
    def hsn_tsn_db() -> dict:
        return {
            "1234": ["qwe", "asd", "bcx"],
            "2345": ["456", "457"],
        }

    def validate_kfz_hsn(
        self,
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        if slot_value.lower() in self.hsn_tsn_db().keys():
            # validation succeeded, set the value of the "hsn" slot to value
            return {"kfz_hsn": slot_value}
        else:
            # validation failed, set this slot to None so that the
            # user will be asked for the slot again
            dispatcher.utter_message(
                "Die angegebene HSN Nummer "
                + slot_value
                + " konnte ich leider nicht finden."
            )
            return {"kfz_hsn": None}

    def validate_kfz_tsn(
        self,
        slot_value: Any,
        dispatcher: CollectingDispatcher,
        tracker: Tracker,
        domain: DomainDict,
    ) -> Dict[Text, Any]:
        kfz_hsn = tracker.get_slot("kfz_hsn")

        if slot_value.lower() in self.hsn_tsn_db()[kfz_hsn]:
            # validation succeeded, set the value of the "tsn" slot to value
            return {"kfz_tsn": slot_value}
        else:
            # validation failed, set this slot to None so that the
            # user will be asked for the slot again
            dispatcher.utter_message(
                "Die angegebene TSN Nummer "
                + slot_value
                + " konnte ich leider nicht finden."
            )
            return {"kfz_tsn": None}


class ResetSlotToChange(Action):
    def name(self) -> Text:

        return "action_reset_slot_to_change"

    async def run(
        self,
        dispatcher,
        tracker: Tracker,
        domain: Dict[Text, Any],
    ) -> List[Dict[Text, Any]]:
        slot_to_change = tracker.slots.get("slot_to_change")
        print("Change Slot " + str(slot_to_change))

        valid_slots_to_change_dict = {}

        if tracker.active_loop.get("name") == "phpv_form":
            valid_slots_to_change_dict = {
                "Versicherungsnehmer": "phpv_beneficiary",
                "Beruf": "phpv_employment_type",
                "Schadenfreiheit": "phpv_had_insurance_claims",
                "Selbstbeteiligung": "phpv_with_contribution",
            }

        elif tracker.active_loop.get("name") == "hrv_form":
            valid_slots_to_change_dict = {
                "Gebäudetyp": "hrv_coverage",
                "Wohnfläche": "hrv_living_area",
                "Straße": "hrv_street",
                "Geburtsdatum": "hrv_birth_date",
                "Schadenfreiheit": "hrv_damage_free",
                "Beruf": "hrv_public_service",
                "Versicherungsbeginn": "hrv_insurance_start_date",
                "Glas Zusatzversicherung": "hrv_glass_surface",
                "Fahrrad Zusatzversicherung": "hrv_bicycle_insurance",
                "Haus und Wohnungsschutzbrief": "hrv_emergency_assistance",
            }

        if slot_to_change in valid_slots_to_change_dict.keys():
            return [SlotSet(valid_slots_to_change_dict[slot_to_change], None)]

        return [FollowupAction("utter_default")]


# TODO Wie integrieren? -> Roman fragen
class ActionProvideSlot(Action):
    def name(self) -> set:
        return "action_provide_slot"

    def run(
        self, dispatcher: CollectingDispatcher, tracker: Tracker, domain: DomainDict
    ):
        # Überprüfe, welcher Slot zuletzt gesetzt wurde
        latest_slot = (
            tracker.latest_message["entities"][-1]["entity"]
            if tracker.latest_message["entities"]
            else None
        )

        print()
        # Hole den Wert des zuletzt gesetzten Slots
        slot_value = (
            tracker.get_slot(latest_slot) if latest_slot else "Kein Slot gefunden"
        )

        # Sende die Antwort mit dem letzten Slot-Wert
        dispatcher.utter_message(
            text=f"Der zuletzt gesetzte Slot ist: {latest_slot} mit dem Wert: {slot_value}"
        )

        return []
