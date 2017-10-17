## What's this?

This is a small helper for me to translate Rubberduck. 

 [![Build Status](https://travis-ci.org/Vogel612/TranslationHelper.svg?branch=master)](https://travis-ci.org/Vogel612/TranslationHelper?branch=master) [![Coverage Status](https://coveralls.io/repos/Vogel612/TranslationHelper/badge.svg?branch=master&service=github)]
(https://coveralls.io/github/Vogel612/TranslationHelper?branch=master)

## Why?

- Manual XML editing is a royal PITA
- I want to do some XML parsing.. it seems to be hard
- I wanted to learn some gradle, MVP

## What does it do?

It displays two sets of resx-files side-by-side. There are special highlights for certain conditions:

- Matching Values for the same key (Yellow background)
- Empty Value for a key (Yellow background)
- Mismatching C# Format specifiers (Orange background)

You can basically choose any resx-file as the basis. The TranslationHelper will automatically find similar files in the same directory and give you options to choose the left and right side of the lineup. 
Any record you doubleclick will be expanded into a translation dialog you can then change the translation in.

- Pressing `return` will open the translation dialog.
- Pressing `return` in the translation dialog will save the changes and close the dialog.
- Pressing `esc` in the translation dialog will abort the current translation.

The TranslationHelper will only change the resx-files if you (as the user) ask it to do so.  
If you close the Helper with unsaved changes, you will be prompted on whether to save the changes (or not).

For any possible bugs I still need to squash and features / changes I'm currently working on,
check the [issues](https://github.com/Vogel612/TranslationHelper/issues)

## License

This project is licensed under the MIT License, see also the LICENSE file
