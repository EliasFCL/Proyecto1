package proyecto1;
import java.io.*;
import java.util.*;
import java.util.regex.*;
/**
 *
 * @author Dell
 */
public class validacionesVar {
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

  //Método para las validaciones de los indicadores
  public static void validarVar(String lineaTrim, int numeroLinea, PrintWriter salida, boolean esperandoUses,
    boolean beginEncontrado, boolean constEncontrado, Set < String > identificadores) {
    //Verificar que los indicadores estén en la posición correcta
    if (esperandoUses || beginEncontrado || !constEncontrado) {
      salida.printf("Error 020. Línea %04d. 'var' debe aparecer después de 'const' y antes de 'begin'%n", numeroLinea);
    }

    String declaracion = lineaTrim.substring(3).trim(); //Quitar "var"

    try {
      //Obtener identificador (lo que está antes de los 2 puntos)
      String identificador = "";
      if (declaracion.contains(":")) {
        identificador = declaracion.split(":")[0].trim(); //El identificador sera lo que esta antes de ":"
        identificadores.add(identificador); //Se añade el identificador a la lista
      }

      if (identificador.isEmpty()) {
        salida.printf("Error 021. Línea %04d. Debe existir un nombre para la variable.%n", numeroLinea);
      }

      if (tipos.Reservada(identificador.toUpperCase())) {
        salida.printf("Error 022. Línea %04d. Se está utilizando una palabra reservada para declarar un identificador%n", numeroLinea);
      }
      //Verificar que la declaracion contenga los 2 puntos
      if (lineaTrim.contains(":")) {
        String espacioDespues = lineaTrim.substring(lineaTrim.indexOf(":") + 1);
        String espacioAntes = lineaTrim.substring(lineaTrim.indexOf(":") - 1);
        if (!espacioDespues.startsWith(" ")) {
          salida.printf("Error 023. Línea %04d. Debe haber un espacio después de los dos puntos en la declaración de la variable.%n", numeroLinea);
        }
        if (!espacioAntes.startsWith(" ")) {
          salida.printf("Error 024. Línea %04d. Debe haber un espacio antes de los dos puntos en la declaración de la variable.%n", numeroLinea);
        }
      } else {
        salida.printf("Error 025. Línea %04d. La declaración debe tener un ':' después del nombre.%n", numeroLinea);
      }

      //Comprobar si empieza por una letra 
      if (!identificador.isEmpty()) {
        if (!Character.isLetter(identificador.charAt(0))) {
          salida.printf("Error 026. Línea %04d. El nombre de la variable '%s' debe comenzar con una letra.%n", numeroLinea, identificador);
        } else if (!identificador.matches("^[a-zA-Z][a-zA-Z_]*$")) {
          salida.printf("Error 027. Línea %04d. El identificador '%s' es inválido. Usar solo letras y guiones bajos.%n", numeroLinea, identificador);
        }
      }

      //Comprobar lo que está después de los 2 puntos
      String tipoVar = "";
      if (lineaTrim.contains(":")) {
        tipoVar = lineaTrim.substring(lineaTrim.indexOf(":") + 1).trim();

        //Quitar el punto y coma final
        if (tipoVar.endsWith(";")) {
          tipoVar = tipoVar.substring(0, tipoVar.length() - 1).trim();
        }

        //Validar que el tipo sea integer, string o word
        if (!(tipoVar.equals("integer") || tipoVar.contains("of integer") ||
            tipoVar.equals("string") ||
            tipoVar.equals("word"))) {
          salida.printf("Error 028. Línea %04d. Tipo de variable inválido: %s%n", numeroLinea, tipoVar);
        } else if (!lineaTrim.endsWith(";")) {
          salida.printf("Error 029. Línea %04d. La declaración del var debe terminar con punto y coma.%n", numeroLinea, tipoVar);
        }
      }
    } catch (Exception e) {
      salida.printf("Error inesperado en validación de 'var' en la línea %04d: %s%n", numeroLinea, e.getMessage());
    }
  }
}