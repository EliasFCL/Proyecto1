package proyecto1;

import java.util.regex.*;
/**
 *
 * @author Dell
 */
public class TablaReservadas {
      enum tipos {
    reservadas("(ABSOLUTE|DOWNTO|BEGIN|DESTRUCTOR|MOD|AND|ELSE|CASE|EXTERNAL|NOT|ARRAY|END|CONST|DIV|PACKED|ASM|FILE|CONSTRUCTOR|" +
      "DO|PROCEDURE|FOR|FORWARD|FUNCTION|GOTO|RECORD|IF|IN|OR|PRIVATE|UNTIL|PROGRAM|REPEAT|STRING|THEN|VAR|WHILE|XOR|WITH|" +
      "TYPE|OF|USES|SET|OBJECT|TO|READKEY|ORD)");
    public final String patron;
    tipos(String s) {
      this.patron = s;
    }
    public static boolean Reservada(String identificador) {
      //Crear un Pattern a partir de reservadas
      Pattern pattern = Pattern.compile(reservadas.patron);
      //Verificar si coincidencias con las reservadas
      Matcher matcher = pattern.matcher(identificador.toUpperCase());
      return matcher.matches();//Devuelve true si es una palabra reservada
    }
  };
}
