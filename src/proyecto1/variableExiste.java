package proyecto1;

import java.io.*;
import java.util.*;

/**
 *
 * @author Dell
 */

public class variableExiste {

  public static void validarFor(String lineaTrim, int numeroLinea, Set < String > identificadores, Set < String > constantes,
    PrintWriter salida) {

    //Validacion del for    
    if (lineaTrim.startsWith("for")) {
      String declaracion = lineaTrim.substring(3).trim(); //Quitar for
      String variable = "";
      boolean encontrado = false;
      //Comprobar que después del for exista un identificador 
      if (declaracion.contains(":")) {
        variable = declaracion.split(":")[0].trim(); //La variable sera lo que esta antes de los 2 puntos
        if (variable.isEmpty()) {
          salida.printf("Error 048. Línea %04d. Después del for debe haber un identificador%n", numeroLinea);
        } else {

          //Comparar uno por uno con los identificadores declarados
          for (String id: identificadores) {
            if (id.equalsIgnoreCase(variable)) {
              encontrado = true;
              break; //Variable encontrada
            }
          }
          if (!encontrado) {
            salida.printf("Error 049. Línea %04d. La variable '%s' no está declarada%n", numeroLinea, variable);
          }
        }
      }
    } else if (lineaTrim.contains(":=")) {
      //Separar por operadores, espacios o paréntesis
      String[] tokens = lineaTrim.split("[():=<>!+\\-*/\\s]+");

      Set < String > palabrasIgnorar = new HashSet < > (Arrays.asList(
        "and", "or", "not", "mod", "div", "then", "else", "do", "ord", "readkey", "for"
      ));

      for (String token: tokens) {
        token = token.trim();
        if (token.isEmpty() || palabrasIgnorar.contains(token.toLowerCase()) || token.matches("\\d+|\\d+;|;")) {
          continue; //Ignorar números, vacíos o palabras reservadas
        }

        //Validar lo que esta antes del corchete
        String antes = token.split("\\[")[0].trim(); //Extraer lo que esta antes del corchete
        if (antes.isEmpty()) {
          salida.printf("Error 050. Línea %04d. No existe una variable usada%n", numeroLinea, antes);
        } else if (!identificadores.contains(antes) && !constantes.contains(antes)) {
          salida.printf("Error 051. Línea %04d. La variable '%s' no está declarada%n", numeroLinea, antes);
        }
        //Validar el contenido de los corchetes
        if (token.contains("[") && token.contains("]")) {
          //Extraer lo que está entre corchetes
          String dentro = token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim();
          //Eliminar el punto y coma
          dentro = dentro.replace(";", "").trim();
          if (dentro.isEmpty()) {
            salida.printf("Error 052. Línea %04d. No existe una variable dentro de los corchetes%n", numeroLinea, antes);
          } else if (!identificadores.contains(dentro) && !dentro.matches("\\d+")) {
            salida.printf("Error 053. Línea %04d. La variable dentro de los corchetes no esta declarada %n", numeroLinea);
          }
        }
      }
    }
  }

  //Validacion de getdate, dec e inc
  public static void validarGetDate(String lineaTrim, int numeroLinea, Set < String > identificadores, PrintWriter salida) {

    int inicial = lineaTrim.indexOf("(");
    int fin = lineaTrim.lastIndexOf(")");

    if (inicial == -1 || fin == -1 || fin < inicial) {
      salida.printf("Error 054. Línea %04d. Se debe tener el paréntesis con una variable%n", numeroLinea);
    } else {
      String contenido = lineaTrim.substring(inicial + 1, fin).trim(); //Extraer lo ques esta entre paréntesis
      String[] parametros = contenido.split(",");
      //Recorrer parametros entre los parentésis
      for (String parametro: parametros) {
        String variable = parametro.trim();
        boolean encontrado = false;
        //Verificar que los identificadores existan
        for (String id: identificadores) {
          if (id.equalsIgnoreCase(variable)) {
            encontrado = true;
            break;
          }
        }

        if (!encontrado) {
          salida.printf("Error 055. Línea %04d. La variable '%s' no está declarada%n", numeroLinea, variable);
        }
      }
    }
  }

  //Validacion del if
  public static void validarIf(String lineaTrim, int numeroLinea, Set < String > identificadores, PrintWriter salida) {

    String condicion = lineaTrim.substring(2).trim(); //Quitar el if

    //Lista con palabras reservadas
    Set < String > palabrasIgnorar = new HashSet < > (Arrays.asList(
      "and", "or", "not", "mod", "div", "then", "else"
    ));

    //Separar la condición por operadores y paréntesis
    String[] tokens = condicion.split("[()=<>!+\\-*/\\s]+");
    //Validar que las variables usadas existan
    for (String token: tokens) {
      //Contenido a ignorar
      String variable = token.trim();
      if (variable.isEmpty()) continue;
      if (palabrasIgnorar.contains(variable.toLowerCase())) continue;
      if (variable.matches("\\d+")) continue; //Ignorar números

      //Obtener la variable dentro de los corchetes
      List < String > contenidoCorchetes = new ArrayList < > ();
      if (variable.contains("[")) {
        int Inicio = variable.indexOf("[");
        int Fin = variable.indexOf("]");
        if (Fin > Inicio) {
          String dentro = variable.substring(Inicio + 1, Fin).trim();
          contenidoCorchetes.add(dentro);
        }
        variable = variable.substring(0, Inicio).trim();
      }

      boolean encontrado = false;
      //Comprobar que la variable exista
      for (String id: identificadores) {
        if (id.equalsIgnoreCase(variable)) {
          encontrado = true;
          break;
        }
      }

      if (!encontrado) {
        salida.printf("Error 056. Línea %04d. La variable '%s' no está declarada%n",
          numeroLinea, variable);
      }
      //Validar las variables dentro de los corchetes
      for (String contenido: contenidoCorchetes) {
        boolean dentroValido = false;
        //Comprobar que el identificador exista
        for (String id: identificadores) {
          if (id.equalsIgnoreCase(contenido)) {
            dentroValido = true;
            break;
          }
        }
        if (!dentroValido) {
          salida.printf("Error 057. Línea %04d. La variable '%s' dentro de corchetes no está declarada%n",
            numeroLinea, contenido);
        }
      }
    }
  }
  //Validaciones del until y while
  public static void validarUntilWhile(String lineaTrim, int numeroLinea, Set < String > identificadores, PrintWriter salida) {

    //Cortar la palabra ("while"/"until") y obtener la condición
    String condicion = lineaTrim.substring(5).trim();

    //Lista de palabras reservadas
    Set < String > palabrasIgnorar = new HashSet < > (Arrays.asList(
      "and", "or", "not", "mod", "div", "then", "else", "do"
    ));

    //Separar la condición por operadores, espacios o paréntesis
    String[] tokens = condicion.split("[()=<>!+\\-*/\\s]+");
    //Recorrer las condiciones 
    for (String token: tokens) {
      token = token.trim();
      if (token.isEmpty() || palabrasIgnorar.contains(token.toLowerCase()) || token.matches("\\d+|\\d+;|;")) {
        continue; //ignorar
      }

      //Tomar la parte antes del corchete
      String antes = token.contains("[") ?
        token.substring(0, token.indexOf("[")).trim() :
        token;

      if (!identificadores.contains(antes)) {
        salida.printf("Error 058. Línea %04d. La variable '%s' no está declarada%n",
          numeroLinea, antes);
      }

      // Validar contenido dentro de corchetes
      if (token.contains("[") && token.contains("]")) {
        String dentro = token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim();
        if (!identificadores.contains(dentro)) {
          salida.printf("Error 059. Línea %04d. La variable '%s' dentro de corchetes no está declarada%n",
            numeroLinea, dentro);
        }
      }
    }
  }
}