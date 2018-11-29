# FigTree for EukRef

Main features in this edition:

- Convenient branch naming with autocomplete (the name of branch is initialized as a `taxonomy` attribute)

- Automatical taxa annotation; For each taxa the `taxonomy` attribute can be build as a sequence of branch names delimited by `;`

- Marking taxa with "to remove" label; this will assign `eukref_remove` attribute to selected taxa, they will be shown with a red outline

- Cleaning fasta file from the taxa, marked with "to remove" attribute in the tree; this will also append taxonomy to each fasta header after the accession number

## How to use

_TODO_
