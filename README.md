# jmerge-pdf
CLI feito usando template do jbang e as dependências:

- picocli
- pdfbox

## Modo de uso

Pode-se usar o [JBang](https://www.jbang.dev/) para executar o CLI conforme abaixo

    jbang MergePdf.java -h

Com o comando acima será visto o comando de ajuda do CLI.

Existem algumas opções de uso, que seriam:

1. Passando como parametro os arquivos;
1. Usando a opção `-d` ou `--dir` e informar o diretório (Pode-se usar esta opção mais de uma vez);
1. Usando as duas anteriores em conjunto.

Para a opção `1` seria conforme abaixo:

    jbang MergePdf.java examples/aaa.pdf examples/bbb.pdf

Para a opção `2` seria da seguinte forma:

    jbang MergePdf.java -d examples/dir1 -d examples/dir2

Por fim, mas não menos importante, a opção `3` seria assim:

    jbang MergePdf.java -d examples/dir1 -d examples/dir2 examples/aaa.pdf examples/bbb.pdf

Pode-se também usar a opção `-o` para customizar o nome do arquivo final gerado.

Caso não deseje baixar o fonte, pode-se também executar o JBang direto para este repositório conforme mostra na [documentação oficial](https://www.jbang.dev/documentation/guide/latest/running.html#remote-file-arguments).
