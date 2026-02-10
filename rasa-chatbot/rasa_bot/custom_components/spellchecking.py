from typing import List

from rasa.engine.graph import GraphComponent
from rasa.engine.recipes.default_recipe import DefaultV1Recipe
from rasa.shared.nlu.constants import TEXT
from rasa.shared.nlu.training_data.message import Message
from spellchecker import SpellChecker

spell = SpellChecker()


@DefaultV1Recipe.register(
    component_types=[DefaultV1Recipe.ComponentType.INTENT_CLASSIFIER],
    is_trainable=False,
)
class CorrectSpelling(GraphComponent):

    name = "Spell_checker"
    provides = ["message"]
    requires = ["message"]
    language_list = ["de"]

    def __init__(self, component_config=None):
        super(CorrectSpelling, self).__init__(component_config)

    def process(self, messages: List[Message]) -> List[Message]:
        """Retrieve the text message, do spelling correction word by word,
        then append all the words and form the sentence,
        pass it to next component of pipeline"""
        for message in messages:
            text = message.get(TEXT)
            if text:
                print("_______________")
                print(f"Original Text: {text}")  # Print original text
                text_split = text.split()

                message.set(TEXT, " ".join(spell.correction(w) for w in text_split))
                print(f"Text after Spellchecking: {message.get(TEXT)}")

        return messages
