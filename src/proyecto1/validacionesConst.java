package proyecto1;

import java.io.*;
import java.util.*;
/**
 *
 * @author Dell
 */
public class validacionesConst {

  public static void validarConst(String lineaTrim, int numeroLinea, PrintWriter salida,
    boolean constCorrecto, Set < String > constantes) {
    try {
      if (!constCorrecto) {
        salida.printf("Error 210. Línea %04d. 'const' debe estar ubicado entre uses y var%n", numeroLinea);
      }

      String declaracion = lineaTrim.substring(5).trim();//Quitar la palabra 'const'

      //Verificar si se tiene un ":" o "="
      if (!declaracion.contains(":") && !declaracion.contains("=")) {
        salida.printf("Error 211. Línea %04d. Después del nombre de la constante debe haber ':' o '='%n", numeroLinea);
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
          salida.printf("Error. Línea %04d. El nombre de la constante '%s' debe comenzar con una letra.%n", numeroLinea, nombreConstante);
        } else if (!nombreConstante.matches("^[a-zA-Z][a-zA-Z_]*$")) {
          salida.printf("Error. Línea %04d. El identificador '%s' es inválido. Usar solo letras y guiones bajos.%n", numeroLinea, nombreConstante);
        }

        if (nombreConstante.isEmpty()) {
          salida.printf("Error 212. Línea %04d. El nombre de la constante no puede ser nulo%n", numeroLinea);
        }

        //Validar "of integer" u "of string"
        if (declaracion.contains(":")) {
          if (declaracion.contains("of integer")) {
            if (!declaracion.matches(".*of\\s+integer\\s*=.*")) {
              salida.printf("Error 214. Línea %04d. Después de 'integer' debe ir '='%n", numeroLinea);
            }
          } else if (declaracion.contains("of string")) {
            if (!declaracion.matches(".*of\\s+string\\s*=.*")) {
              salida.printf("Error 216. Línea %04d. Después de 'string' debe ir '='%n", numeroLinea);
            }
          } else {
            salida.printf("Error 213. Línea %04d. Falta 'of integer' u 'of string' en la declaración%n", numeroLinea);
          }
        }

        String valor = declaracion.substring(declaracion.indexOf("=") + 1).trim();
        //Quitar el ';' final si existe
        if (valor.endsWith(";")) {
          valor = valor.substring(0, valor.length() - 1).trim();
        }

        if (valor.isEmpty()) {
          salida.printf("Error 217. Línea %04d. La constante '%s' debe tener un valor después del '='%n", numeroLinea, nombreConstante);
        }
      }

      if (!lineaTrim.endsWith(";")) {
        salida.printf("Error 210. Línea %04d. 'const' debe contener un ';' al final%n", numeroLinea);
      }

      //Dividir por coma entre constantes
      String[] partes = declaracion.split("\\s*,\\s*(?=[a-zA-Z])");//Separa solo cuando empieza con letra
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