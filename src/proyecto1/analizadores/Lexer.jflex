package proyecto1.analizadores;
import java_cup.runtime.Symbol;

%%
%class Lexer
%public
%unicode
%line
%column
%cup

// Macros básicas
LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]
IntegerLiteral = [0-9]+
FloatLiteral   = [0-9]+ \. [0-9]+
Identifier     = [a-zA-Z_] [a-zA-Z0-9_]*

// Estado para comentarios multilínea
%state MULTILINE_COMMENT

%%

<YYINITIAL> {
  // Palabras reservadas (Ejemplos iniciales)
  "mage"           { return new Symbol(sym.MAGE, yyline + 1, yycolumn + 1, yytext()); }
  "warrior"        { return new Symbol(sym.WARRIOR, yyline + 1, yycolumn + 1, yytext()); }
  "if"             { return new Symbol(sym.IF, yyline + 1, yycolumn + 1, yytext()); }

  // Operadores y símbolos
  "=="             { return new Symbol(sym.EQEQ, yyline + 1, yycolumn + 1, yytext()); }
  "<="             { return new Symbol(sym.LTEQ, yyline + 1, yycolumn + 1, yytext()); }
  "{"              { return new Symbol(sym.LBRACE, yyline + 1, yycolumn + 1, yytext()); }
  "}"              { return new Symbol(sym.RBRACE, yyline + 1, yycolumn + 1, yytext()); }

  // Literales e Identificadores
  {IntegerLiteral} { return new Symbol(sym.INTEGER, yyline + 1, yycolumn + 1, yytext()); }
  {FloatLiteral}   { return new Symbol(sym.FLOAT, yyline + 1, yycolumn + 1, yytext()); }
  {Identifier}     { return new Symbol(sym.ID, yyline + 1, yycolumn + 1, yytext()); }

  // Comentarios de una línea
  "//" {InputCharacter}* { /* Ignorar */ }

  // Inicio de comentario multilínea
  "/*"             { yybegin(MULTILINE_COMMENT); }

  {WhiteSpace}     { /* Ignorar */ }
}

<MULTILINE_COMMENT> {
  "*/"             { yybegin(YYINITIAL); }
  [^]              { /* Ignorar contenido */ }
}

// Fallback para Errores Léxicos
[^]                { 
    System.out.println("Error Léxico: " + yytext() + " en línea " + (yyline + 1)); 
    // Aquí luego agregaremos la lógica para guardar el error en tu reporte visual
}
