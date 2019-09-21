text = """
erf hlxhucf uk fvougqvz qc eu expvckuxb gpep cu erpe qe opv wf hxuhfxjd pvg cpkfjd ouvclbfg wd p gqkkfxfve edhf uk cdcefb fz wqvpxd gpep wfqvz cfve unfx fbpqj ux nqfsqvz chfoqpj orpxpoefxc uv p sfw hpzf erf zupj qc vue eu tffh qvkuxbpequv cfoxfe wle xperfx eu fvclxf erpe qec pwjf eu wf hxuhfxjd ouvclbfg fvougqvz expvckuxbc gpep qveu pvuerfx kuxbpe lcqvz p corfbf erpe qc hlwjqojd pnpqjpwjf cu erpe qe opv fpcqjd wf xfnfxcfg qe gufc vue xfilqxf p tfd pc erf uvjd erqvz xfilqxfg eu gfougf qe qc erf pjzuxqerb erpe spc lcfg eu fvougf qe erf hlxhucf uk fvoxdhequv qc eu expvckuxb gpep qv uxgfx eu tffh qe cfoxfe kxub uerfxc fz cfvgqvz cubfuvf p cfoxfe jfeefx erpe uvjd erfd cruljg wf pwjf eu xfpg ux cfolxfjd cfvgqvz p hpccsuxg unfx erf qvefxvfe xperfx erpv kuolcqvz uv lcpwqjqed erf zupj qc eu fvclxf erf gpep opvvue wf ouvclbfg wd pvduvf uerfx erpv erf qvefvgfg xfoqhqfvec fvoxdhequv expvckuxbc gpep qveu pvuerfx kuxbpe qv clor p spd erpe uvjd chfoqkqo qvgqnqglpjc opv xfnfxcf erf expvckuxbpequv qe lcfc p tfd srqor qc tfhe cfoxfe qv ouvylvoequv sqer erf hjpqvefae pvg erf pjzuxqerb qv uxgfx eu hfxkuxb erf fvoxdhequv uhfxpequv pc clor erf oqhrfxefae pjzuxqerb pvg tfd pxf pjj xfilqxfg eu xfelxv eu erf hjpqvefae juz kqvf cpg pbulve tqvg wjpvtfe hucqequv fpzfx rujf rlve
"""

key = {
    "j": "l",
    "p": "a"
}

result = ""
for letter in text:
    if letter in key:
        letter = key[letter]
    result += letter
print(result)