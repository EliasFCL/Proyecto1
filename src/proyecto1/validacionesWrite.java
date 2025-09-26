package proyecto1;

import java.io.*;
import java.util.*;
/**
 *
 * @author Dell
 */
public class validacionesWrite {

  public static void validarWrite(String lineaTrim, int numeroLinea, PrintWriter salida, boolean beginEncontrado,
    Set < String > constantes, Set < String > identificadores){

    if (!beginEncontrado) {
      salida.printf("Error 042. Línea %04d. 'write' no puede estar antes de begin'%n", numeroLinea);
    }
    //Validar los paréntesis
    int inicio = lineaTrim.indexOf("(");
    int fin = lineaTrim.lastIndexOf(")");
    if (inicio == -1 || fin == -1) {
      salida.printf("Error 043. Línea %04d. La línea del 'write' no contiene uno de sus paréntesis%n", numeroLinea);
    } else {
      String contenido = lineaTrim.substring(inicio + 1, fin).trim();//Se toma lo que esta después del  primer paréntesis y antes del ultimo
      if (contenido.isEmpty()) {
        salida.printf("Error 044. Línea %04d. Los paréntesis no pueden ir vacíos%n", numeroLinea);
      } else {
        //Comprobar si las comillas están bien cerradas
        int cantComillas = 0;
        //Verificar si hay una coma dentro del parentésis
        for (int i = 0; i < contenido.length(); i++) {
          char caracter = contenido.charAt(i);
          if(caracter == '\"'){
            salida.printf("Error 076. Línea %04d. La comilla doble no es valida, debe ser simple%n", numeroLinea);
          }
          if (caracter == '\'') {
            cantComillas++;
          } 
        }
        if(cantComillas % 2 != 0){
             salida.printf("Error 098. Línea %04d. Faltan comillas por cerrar%n", numeroLinea);
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
        
        //Variables dentro corchetes
        String corchete = contenidoToken;
        String contCorchete = null;
        if (contenidoToken.contains("[")) {
          corchete = token.split("\\[")[0].trim();
          contCorchete = contenidoToken.substring(contenidoToken.indexOf("[") + 1, contenidoToken.indexOf("]")).trim();
        }

        //Quitar 2 puntos seguidos de 1 o mas dígitos(para la línea 60)
        corchete = corchete.replaceAll(":\\d+$", "");

        boolean encontrado = false;
        //Validar lo que esta al lado de los corchetes
        if (identificadores.contains(corchete) || constantes.contains(corchete)) {
          encontrado = true;
        }
        //Si el contenido no es nulo y no es un identificador entonces el contenido no es valido
        if (contCorchete != null && !identificadores.contains(contCorchete)) {
          encontrado = false;
        }

        if (!encontrado) {
          salida.printf("Error 046. Línea %04d. La palabra '%s' no es una variable o constante válida%n", numeroLinea, contenidoToken);
        }
      }
    }

    if (!lineaTrim.endsWith(";")) {
      salida.printf("Error 047. Línea %04d. Falta el punto y coma del write%n", numeroLinea);
    }
  }
}