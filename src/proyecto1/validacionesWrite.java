//Resetear la lectura(mark y reset): https://www.tutorialspoint.com/java/io/bufferedreader_reset.htm 

package proyecto1;

import java.io.*;
import java.util.*;

/**
 *
 * @author Dell
 */
public class validacionesWrite {

  public static void validarWrite(String linea, String lineaTrim, int numeroLinea, PrintWriter salida, boolean beginEncontrado,
    Set < String > constantes, Set < String > identificadores, BufferedReader entrada) throws IOException {

    if (!beginEncontrado) {
      salida.printf("Error 216. Línea %04d. 'write' no puede estar antes de begin'%n", numeroLinea);
    }
    //Validar los paréntesis
    int inicio = linea.indexOf("(");
    int fin = linea.lastIndexOf(")");
    if (inicio == -1 || fin == -1) {
      salida.printf("Error 217. Línea %04d. La línea del 'write' no contiene uno de sus paréntesis%n", numeroLinea);
    } else {
      String contenido = linea.substring(inicio + 1, fin).trim();
      if (contenido.isEmpty()) {
        salida.printf("Error 219. Línea %04d. Los paréntesis no pueden ir vacíos%n", numeroLinea);
      } else {
        //Comprobar si las comillas están bien cerradas
        boolean dentroComilla = false;
        boolean errorCadena = false;
        //Verificar si hay una coma dentro del parentésis
        for (int i = 0; i < contenido.length(); i++) {
          char caracter = contenido.charAt(i);
          if (caracter == '\'') {
            dentroComilla = !dentroComilla;
            if (!dentroComilla) errorCadena = false;
          } else if (dentroComilla && caracter == ',' && !errorCadena) {
            salida.printf("Error 220. Línea %04d. Falta comilla antes de la coma%n", numeroLinea);
            errorCadena = true;
          }
        }
      }

      //Validación de variables y constantes
      String[] tokens = contenido.split(",");

      //Validar contenido de los parentésis
      for (String token: tokens) {
        String contenidoToken = token.trim();
        //Contenido a ignorar
        if (contenidoToken.isEmpty()) continue;
        if (contenidoToken.matches("('.*'|#\\d+|'[^']*'#\\d+|#\\d+'.*')")) continue;
        if (contenidoToken.matches("((#\\d+)|('.*?'))+")) continue;
        if (constantes.contains(contenidoToken)) continue;
        if (contenidoToken.matches("\\d+") || contenidoToken.equals(";")) continue;

        //Variables dentro corchetes
        String corchete = contenidoToken;
        String contCorchete = null;
        if (contenidoToken.contains("[")) {
          corchete = contenidoToken.substring(0, contenidoToken.indexOf("[")).trim();
          contCorchete = contenidoToken.substring(contenidoToken.indexOf("[") + 1, contenidoToken.indexOf("]")).trim();
        }

        //Quitar 2 puntos seguidos de 1 o mas dígitos(para la línea 60)
        corchete = corchete.replaceAll(":\\d+$", "");

        boolean encontrado = false;
        if (identificadores.contains(corchete) || constantes.contains(corchete)) {
          encontrado = true;
        }
        //Si el contenido no es nulo y no es un identificador entonces el contenido no es valido
        if (contCorchete != null && !identificadores.contains(contCorchete)) {
          encontrado = false;
        }

        if (!encontrado) {
          salida.printf("Error 233. Línea %04d. La palabra '%s' no es una variable o constante válida%n", numeroLinea, contenidoToken);
        }
      }
    }

    if (!lineaTrim.endsWith(";")) {
      //Marcar posición del reader (al inicio de la línea write)
      entrada.mark(1000);
      String siguienteLinea = entrada.readLine();//Leer la siguiente línea
      if (siguienteLinea != null) {
        String siguienteTrim = siguienteLinea.trim().toLowerCase();
        //Si lo que sigue después del write es un 'end' se omite el error, pero si es 'end;' mostrarlo
        if (siguienteTrim.equals("end")) {
          //No hacer nada
        } else if (siguienteTrim.equals("end;") || siguienteTrim.startsWith("write")) {
          salida.printf("Error 222. Línea %04d. Falta el punto y coma del write%n", numeroLinea);
        }
      }
      //Volver al inicio de la línea leída(posición marcada)
      entrada.reset();
    }
  }
}