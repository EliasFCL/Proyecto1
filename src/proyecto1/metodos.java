package proyecto1;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 *
 * @author Dell
 */
public class metodos {
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
      return matcher.matches(); //Devuelve true si es una palabra reservada
    }
  };

  public static void validarCorchetes(String token, int numeroLinea, Set < String > identificadores, PrintWriter salida) {
    //Si la línea contiene ambos corchetes
    if (token.contains("[") && token.contains("]")) {
      //Extraer lo que esta despues de '[' y antes de ']'
      String dentro = token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim();
      dentro = dentro.replace(";", "").trim();//Eliminar punto y coma si lo hay

      if (dentro.isEmpty()) {
        salida.printf("Error 052. Línea %04d. No existe una variable dentro de los corchetes%n", numeroLinea);
      } else if (!identificadores.contains(dentro) && !dentro.matches("\\d+")) {
        salida.printf("Error 053. Línea %04d. La variable dentro de los corchetes no está declarada%n", numeroLinea);
      }
    }
  }
}