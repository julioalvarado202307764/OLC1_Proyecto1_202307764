package proyecto1.analizadores;
import java_cup.runtime.Symbol;

%%
%class Lexer
%public
%unicode
%line
%column
%cup
%{
    //almacena tokens reconocidos
    public java.util.ArrayList<TokenInfo> listaTokens = new java.util.ArrayList<>();
    public java.util.ArrayList<ErrorInfo> listaErrores = new java.util.ArrayList<>();

     // Guarda la posición donde inició el comentario multilínea actual
    private int comentarioLinea, comentarioColumna;
    //guarda en la lista y retorna symbol a cup
    private java_cup.runtime.Symbol token(int tipoSym, String nombreTipo, Object valor) {
        listaTokens.add(new TokenInfo(yytext(), nombreTipo, yyline + 1, yycolumn + 1));
        return new java_cup.runtime.Symbol(tipoSym, yyline + 1, yycolumn + 1, valor);
    }
%}
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
  "mage"           { return token(sym.MAGE, "Reservada", yytext()); }
  "warrior"        { return token(sym.WARRIOR, "Reservada", yytext()); }
  "if"             { return token(sym.IF, "Reservada", yytext()); }

  // Operadores y símbolos
  "=="             { return token(sym.EQEQ, "Operador Relacional", yytext()); }
  "!="              { return token(sym.NEQ, "Operador Relacional", yytext()); }
  "<="              { return token(sym.LTEQ, "Operador Relacional", yytext()); }
  ">="              { return token(sym.GTEQ, "Operador Relacional", yytext()); }
  "<"               { return token(sym.LT, "Operador Relacional", yytext()); }
  ">"               { return token(sym.GT, "Operador Relacional", yytext()); }
  "&&"              { return token(sym.AND, "Operador Lógico", yytext()); }
  "||"              { return token(sym.OR, "Operador Lógico", yytext()); }
  "!"               { return token(sym.NOT, "Operador Lógico", yytext()); }
  "{"               { return token(sym.LBRACE, "Símbolo de Agrupación", yytext()); }
  "}"               { return token(sym.RBRACE, "Símbolo de Agrupación", yytext()); }
  "["               { return token(sym.LBRACKET, "Símbolo de Agrupación", yytext()); }
  "]"               { return token(sym.RBRACKET, "Símbolo de Agrupación", yytext()); }
  "("               { return token(sym.LPAREN, "Símbolo de Agrupación", yytext()); }
  ")"               { return token(sym.RPAREN, "Símbolo de Agrupación", yytext()); }
  ":"               { return token(sym.COLON, "Signo de Puntuación", yytext()); }
  ","               { return token(sym.COMMA, "Signo de Puntuación", yytext()); }
  // --- Acciones de Mago ---
  "ARCANE_BOLT"    { return token(sym.ARCANE_BOLT, "Acción Mago", yytext()); }
  "FIREBALL"       { return token(sym.FIREBALL, "Acción Mago", yytext()); }
  "MAGIC_BARRIER"  { return token(sym.MAGIC_BARRIER, "Acción Mago", yytext()); }
  "HEALING_RUNE"   { return token(sym.HEALING_RUNE, "Acción Mago", yytext()); }
  "MEDITATE"       { return token(sym.MEDITATE, "Acción Mago", yytext()); }

  // --- Acciones de Guerrero ---
  "SLASH"          { return token(sym.SLASH, "Acción Guerrero", yytext()); }
  "HEAVY_STRIKE"   { return token(sym.HEAVY_STRIKE, "Acción Guerrero", yytext()); }
  "SHIELD_BLOCK"   { return token(sym.SHIELD_BLOCK, "Acción Guerrero", yytext()); }
  "WAR_CRY"        { return token(sym.WAR_CRY, "Acción Guerrero", yytext()); }
  "REST"           { return token(sym.REST, "Acción Guerrero", yytext()); }
  
  // --- Variables de Entorno ---
  "round_number"      { return token(sym.ROUND_NUMBER, "Variable de Entorno", yytext()); }
  "total_rounds"      { return token(sym.TOTAL_ROUNDS, "Variable de Entorno", yytext()); }
  "self_health"       { return token(sym.SELF_HEALTH, "Variable de Entorno", yytext()); }
  "opponent_health"   { return token(sym.OPPONENT_HEALTH, "Variable de Entorno", yytext()); }
  "self_resource"     { return token(sym.SELF_RESOURCE, "Variable de Entorno", yytext()); }
  "opponent_resource" { return token(sym.OPPONENT_RESOURCE, "Variable de Entorno", yytext()); }
  "self_score"        { return token(sym.SELF_SCORE, "Variable de Entorno", yytext()); }
  "opponent_score"    { return token(sym.OPPONENT_SCORE, "Variable de Entorno", yytext()); }
  "self_history"      { return token(sym.SELF_HISTORY, "Variable de Entorno", yytext()); }
  "opponent_history"  { return token(sym.OPPONENT_HISTORY, "Variable de Entorno", yytext()); }
  "random"            { return token(sym.RANDOM, "Variable de Entorno", yytext()); }

  // --- Funciones del Sistema ---
  "get_move"          { return token(sym.GET_MOVE, "Función del Sistema", yytext()); }
  "last_move"         { return token(sym.LAST_MOVE, "Función del Sistema", yytext()); }
  "get_moves_count"   { return token(sym.GET_MOVES_COUNT, "Función del Sistema", yytext()); }
  "get_last_n_moves"  { return token(sym.GET_LAST_N_MOVES, "Función del Sistema", yytext()); }
  
  // --- Control de Flujo y Estructura ---
  "initial"           { return token(sym.INITIAL, "Control de Flujo", yytext()); }
  "rules"             { return token(sym.RULES, "Control de Flujo", yytext()); }
  "then"              { return token(sym.THEN, "Control de Flujo", yytext()); }
  "else"              { return token(sym.ELSE, "Control de Flujo", yytext()); }
  "match"             { return token(sym.MATCH, "Estructura", yytext()); }
  "players"           { return token(sym.PLAYERS, "Estructura", yytext()); }
  "rounds"            { return token(sym.ROUNDS, "Estructura", yytext()); }
  "scoring"           { return token(sym.SCORING, "Estructura", yytext()); }
  "bonuses"           { return token(sym.BONUSES, "Estructura", yytext()); }
  "main"              { return token(sym.MAIN, "Estructura", yytext()); }
  "run"               { return token(sym.RUN, "Estructura", yytext()); }
  "with"              { return token(sym.WITH, "Estructura", yytext()); }
  "seed"              { return token(sym.SEED, "Estructura", yytext()); }
 
  // --- Parámetros de Puntuación ---
  "damage_point"            { return token(sym.DAMAGE_POINT, "Parámetro de Puntuación", yytext()); }
  "healing_point"           { return token(sym.HEALING_POINT, "Parámetro de Puntuación", yytext()); }
  "successful_defense"      { return token(sym.SUCCESSFUL_DEFENSE, "Parámetro de Puntuación", yytext()); }
  "victory_bonus"           { return token(sym.VICTORY_BONUS, "Parámetro de Puntuación", yytext()); }
  "failed_action_penalty"   { return token(sym.FAILED_ACTION_PENALTY, "Parámetro de Puntuación", yytext()); }
  "mage_combo"              { return token(sym.MAGE_COMBO, "Parámetro de Puntuación", yytext()); }
  "mage_combo_points"       { return token(sym.MAGE_COMBO_POINTS, "Parámetro de Puntuación", yytext()); }
  "warrior_combo"           { return token(sym.WARRIOR_COMBO, "Parámetro de Puntuación", yytext()); }
  "warrior_combo_points"    { return token(sym.WARRIOR_COMBO_POINTS, "Parámetro de Puntuación", yytext()); }
  "low_health_victory"      { return token(sym.LOW_HEALTH_VICTORY, "Parámetro de Puntuación", yytext()); }
  
  
// Literales e Identificadores
  {IntegerLiteral} { return token(sym.INTEGER, "Entero", yytext()); }
  {FloatLiteral}   { return token(sym.FLOAT, "Decimal", yytext()); }
  {Identifier}     { return token(sym.ID, "Identificador", yytext()); }

  // Comentarios de una línea
  "//" {InputCharacter}* { /* Ignorar */ }

  // Inicio de comentario multilínea
  "/*"             { comentarioLinea = yyline + 1; comentarioColumna = yycolumn + 1; yybegin(MULTILINE_COMMENT); }

  {WhiteSpace}     { /* Ignorar */ }

  //FALLBACK PARA ERRORES LÉXICOS
  [^] { 
      String desc = "El carácter '" + yytext() + "' no pertenece al lenguaje";
      listaErrores.add(new ErrorInfo("Léxico", desc, yyline + 1, yycolumn + 1)); 
  }
} 

// --- ESTADO PARA COMENTARIOS MULTILÍNEA ---
<MULTILINE_COMMENT> {
  "*/"             { yybegin(YYINITIAL); }
  [^]              { /* Ignorar contenido, aquí no va el error */ }

  <<EOF>>          {
      listaErrores.add(new ErrorInfo(
          "Léxico",
          "Comentario multilínea sin cerrar (iniciado en línea " + comentarioLinea + ", columna " + comentarioColumna + ")",
          comentarioLinea,
          comentarioColumna
      ));
      throw new RuntimeException("Error léxico fatal: comentario multilínea sin cerrar.");
  }
}