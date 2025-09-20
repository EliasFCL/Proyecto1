package proyecto1;

import java.io.*;
import java.util.*;
import java.util.regex.*;
/**
 *
 * @author Dell
 */
public class validacionesConst {

  public static void validarConst(String lineaTrim, int numeroLinea, PrintWriter salida,
    Set < String > constantes) {
    enum tipos {
      reservadas("(ABSOLUTE|DOWNTO|BEGIN|DESTRUCTOR|MOD|AND|ELSE|CASE|EXTERNAL|NOT|ARRAY|END|CONST|DIV|PACKED|ASM|FILE|CONSTRUCTOR|" +
        "DO|PROCEDURE|FOR|FORWARD|FUNCTION|GOTO|RECORD|IF|IN|OR|PRIVATE|UNTIL|PROGRAM|REPEAT|STRING|THEN|VAR|WHILE|XOR|WITH|" +
        "TYPE|OF|USES|SET|OBJECT|TO)");

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

    try {
      String declaracion = lineaTrim.substring(5).trim(); //Quitar la palabra 'const'

      //Verificar si se tiene un ":" o "="
      if (!declaracion.contains(":") && !declaracion.contains("=")) {
        salida.printf("Error 033. Línea %04d. Después del nombre de la constante debe haber ':' o '='%n", numeroLinea);
      } else {
        //Tomar la parte antes de ":" o "="
        String nombreConstante;
        if (declaracion.contains(":")) {
          nombreConstante = declaracion.split(":", 2)[0].trim();
        } else {
          nombreConstante = declaracion.split("=", 2)[0].trim();
        }

        //Validaciones del identificador
        if (!Character.isLetter(nombreConstante.charAt(0))) {
          salida.printf("Error 034. Línea %04d. El nombre de la constante '%s' debe comenzar con una letra.%n", numeroLinea, nombreConstante);
        } else if (!nombreConstante.matches("^[a-zA-Z][a-zA-Z_]*$")) {
          salida.printf("Error 035. Línea %04d. El identificador '%s' es inválido. Usar solo letras y guiones bajos.%n", numeroLinea, nombreConstante);
        } else if (tipos.Reservada(nombreConstante.toUpperCase())) {
          salida.printf("Error 036. Línea %04d. Se está usando una palabra reservada para la constante.%n", numeroLinea, nombreConstante);
        }

        if (nombreConstante.isEmpty()) {
          salida.printf("Error 037. Línea %04d. El nombre de la constante no puede ser nulo%n", numeroLinea);
        }

        //Validar "of integer" u "of string"
        if (declaracion.contains(":")) {
          if (declaracion.contains("of integer")) {
            if (!declaracion.matches(".*of\\s+integer\\s*=.*")) {
              salida.printf("Error 038. Línea %04d. Después de 'integer' debe ir '='%n", numeroLinea);
            }
          } else if (declaracion.contains("of string")) {
            if (!declaracion.matches(".*of\\s+string\\s*=.*")) {
              salida.printf("Error 039. Línea %04d. Después de 'string' debe ir '='%n", numeroLinea);
            }
          } else {
            salida.printf("Error 040. Línea %04d. Falta 'of integer' u 'of string' en la declaración%n", numeroLinea);
          }
        }

        String valor = declaracion.substring(declaracion.indexOf("=") + 1).trim();
        //Quitar el ';'
        if (valor.endsWith(";")) {
          valor = valor.substring(0, valor.length() - 1).trim();
        }

        if (valor.isEmpty()) {
          salida.printf("Error 041. Línea %04d. La constante '%s' debe tener un valor después del '='%n", numeroLinea, nombreConstante);
        }
      }

      //Dividir por coma entre constantes
      String[] partes = declaracion.split("\\s*,\\s*(?=[a-zA-Z])"); //Separa solo cuando empieza con letra
      //Extraer el nombre de cada constante 
      for (String parte: partes) {
        parte = parte.trim();
        String nombreConstante = parte.split("=")[0].split(":")[0].trim();
        if (!nombreConstante.isEmpty()) {
          constantes.add(nombreConstante);
        }
      }
    } catch (Exception e) {
      salida.printf("Error inesperado en validación de 'const' en la línea %04d: %s%n", numeroLinea, e.getMessage());
    }
  }
}