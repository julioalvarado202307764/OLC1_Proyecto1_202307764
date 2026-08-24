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
  // --- Acciones de Mago ---
  "ARCANE_BOLT"    { return new Symbol(sym.ARCANE_BOLT, yyline + 1, yycolumn + 1); }
  "FIREBALL"       { return new Symbol(sym.FIREBALL, yyline + 1, yycolumn + 1); }
  "MAGIC_BARRIER"  { return new Symbol(sym.MAGIC_BARRIER, yyline + 1, yycolumn + 1); }
  "HEALING_RUNE"   { return new Symbol(sym.HEALING_RUNE, yyline + 1, yycolumn + 1); }
  "MEDITATE"       { return new Symbol(sym.MEDITATE, yyline + 1, yycolumn + 1); }

  // --- Acciones de Guerrero ---
  "SLASH"          { return new Symbol(sym.SLASH, yyline + 1, yycolumn + 1); }
  "HEAVY_STRIKE"   { return new Symbol(sym.HEAVY_STRIKE, yyline + 1, yycolumn + 1); }
  "SHIELD_BLOCK"   { return new Symbol(sym.SHIELD_BLOCK, yyline + 1, yycolumn + 1); }
  "WAR_CRY"        { return new Symbol(sym.WAR_CRY, yyline + 1, yycolumn + 1); }
  "REST"           { return new Symbol(sym.REST, yyline + 1, yycolumn + 1); }
  
  // --- Variables de Entorno ---
  "round_number"      { return new Symbol(sym.ROUND_NUMBER, yyline + 1, yycolumn + 1); }
  "total_rounds"      { return new Symbol(sym.TOTAL_ROUNDS, yyline + 1, yycolumn + 1); }
  "self_health"       { return new Symbol(sym.SELF_HEALTH, yyline + 1, yycolumn + 1); }
  "opponent_health"   { return new Symbol(sym.OPPONENT_HEALTH, yyline + 1, yycolumn + 1); }
  "self_resource"     { return new Symbol(sym.SELF_RESOURCE, yyline + 1, yycolumn + 1); }
  "opponent_resource" { return new Symbol(sym.OPPONENT_RESOURCE, yyline + 1, yycolumn + 1); }
  "self_score"        { return new Symbol(sym.SELF_SCORE, yyline + 1, yycolumn + 1); }
  "opponent_score"    { return new Symbol(sym.OPPONENT_SCORE, yyline + 1, yycolumn + 1); }
  "self_history"      { return new Symbol(sym.SELF_HISTORY, yyline + 1, yycolumn + 1); }
  "opponent_history"  { return new Symbol(sym.OPPONENT_HISTORY, yyline + 1, yycolumn + 1); }
  "random"            { return new Symbol(sym.RANDOM, yyline + 1, yycolumn + 1); }

  // --- Funciones del Sistema ---
  "get_move"          { return new Symbol(sym.GET_MOVE, yyline + 1, yycolumn + 1); }
  "last_move"         { return new Symbol(sym.LAST_MOVE, yyline + 1, yycolumn + 1); }
  "get_moves_count"   { return new Symbol(sym.GET_MOVES_COUNT, yyline + 1, yycolumn + 1); }
  "get_last_n_moves"  { return new Symbol(sym.GET_LAST_N_MOVES, yyline + 1, yycolumn + 1); }
  
  // --- Control de Flujo y Estructura ---
  "initial"                 { return new Symbol(sym.INITIAL, yyline + 1, yycolumn + 1); }
  "rules"                   { return new Symbol(sym.RULES, yyline + 1, yycolumn + 1); }
  "then"                    { return new Symbol(sym.THEN, yyline + 1, yycolumn + 1); }
  "else"                    { return new Symbol(sym.ELSE, yyline + 1, yycolumn + 1); }
  "match"                   { return new Symbol(sym.MATCH, yyline + 1, yycolumn + 1); }
  "players"                 { return new Symbol(sym.PLAYERS, yyline + 1, yycolumn + 1); }
  "rounds"                  { return new Symbol(sym.ROUNDS, yyline + 1, yycolumn + 1); }
  "scoring"                 { return new Symbol(sym.SCORING, yyline + 1, yycolumn + 1); }
  "bonuses"                 { return new Symbol(sym.BONUSES, yyline + 1, yycolumn + 1); }
  "main"                    { return new Symbol(sym.MAIN, yyline + 1, yycolumn + 1); }
  "run"                     { return new Symbol(sym.RUN, yyline + 1, yycolumn + 1); }
  "with"                    { return new Symbol(sym.WITH, yyline + 1, yycolumn + 1); }
  "seed"                    { return new Symbol(sym.SEED, yyline + 1, yycolumn + 1); }

  // --- Parámetros de Puntuación ---
  "damage_point"            { return new Symbol(sym.DAMAGE_POINT, yyline + 1, yycolumn + 1); }
  "healing_point"           { return new Symbol(sym.HEALING_POINT, yyline + 1, yycolumn + 1); }
  "successful_defense"      { return new Symbol(sym.SUCCESSFUL_DEFENSE, yyline + 1, yycolumn + 1); }
  "victory_bonus"           { return new Symbol(sym.VICTORY_BONUS, yyline + 1, yycolumn + 1); }
  "failed_action_penalty"   { return new Symbol(sym.FAILED_ACTION_PENALTY, yyline + 1, yycolumn + 1); }
  "mage_combo"              { return new Symbol(sym.MAGE_COMBO, yyline + 1, yycolumn + 1); }
  "mage_combo_points"       { return new Symbol(sym.MAGE_COMBO_POINTS, yyline + 1, yycolumn + 1); }
  "warrior_combo"           { return new Symbol(sym.WARRIOR_COMBO, yyline + 1, yycolumn + 1); }
  "warrior_combo_points"    { return new Symbol(sym.WARRIOR_COMBO_POINTS, yyline + 1, yycolumn + 1); }
  "low_health_victory"      { return new Symbol(sym.LOW_HEALTH_VICTORY, yyline + 1, yycolumn + 1); }
  
  
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
