package proyecto1;

import java.io.*;
import java.util.*;

/**
 *
 * @author Dell
 */

public class variableExiste {

  public static void validarFor(String lineaTrim, String linea, int numeroLinea,
    Set < String > identificadores, Set < String > constantes,
    PrintWriter salida) {
    
    //Validacion del for y :=
    if (lineaTrim.startsWith("for") || lineaTrim.contains(":=")) {
      if (lineaTrim.startsWith("for")) {
        String[] tokens = lineaTrim.split("\\s+"); //Separar por espacios
        if (tokens.length < 2) {
          salida.printf("Error 233. Línea %04d. Despues del for debe haber un identificador%n", numeroLinea);
        } else {
          String varFor = tokens[1]; //La variable que sigue a 'for'
          //Extraer lo que esta antes del ":"
          if (varFor.contains(":")) {
            varFor = varFor.substring(0, varFor.indexOf(":"));
          }
          boolean encontrado = false;

          //Comparar uno por uno con los identificadores declarados
          for (String id: identificadores) {
            if (id.equalsIgnoreCase(varFor)) {
              encontrado = true;
              break; //Variable encontrada
            }
          }

          if (!encontrado) {
            salida.printf("Error 233. Línea %04d. La variable '%s' no está declarada%n", numeroLinea, varFor);
          }
        }
      } else if (lineaTrim.contains(":=")) {
        String[] lados = lineaTrim.split(":=", 2);
        String ladoIzq = lados[0].trim();
        String ladoDer = lados[1].trim();

        //Extraer corchete del lado izquierdo
        String idIzq = ladoIzq.replaceAll("\\[.*?\\]", "").replace(";", "").trim();
        //Validar que los corchetes tengan contenido
        int inicial = ladoIzq.indexOf("[");
        int fin = ladoIzq.indexOf("]");
        int iniDer = ladoDer.indexOf("[");
        int finDer = ladoDer.indexOf("]");

        if (inicial != -1 && fin != -1) {
          if (fin <= inicial + 1) {
            salida.printf("Error 236. Línea %04d. Los corchetes del lado izquierdo están vacíos%n", numeroLinea);
          }
        }
        if (iniDer != -1 && finDer != -1) {
          if (finDer <= iniDer + 1) {
            salida.printf("Error 237. Línea %04d. Los corchetes del lado derecho están vacíos%n", numeroLinea);
          }
        }
        if (!identificadores.contains(idIzq) && !constantes.contains(idIzq)) {
          salida.printf("Error 233. Línea %04d. La variable '%s' no está declarada%n",
            numeroLinea, idIzq);
        }
        //Lista con palabras reservadas
        Set < String > palabrasIgnorar = new HashSet < > (Arrays.asList(
          "and", "or", "not", "mod", "div", "then", "else", "do", "ord", "readkey","for"
        ));
        //Validar que los corchetes sean correctos
        if (inicial != -1 && fin != -1 && fin > inicial + 1) {
          String dentro = ladoIzq.substring(inicial + 1, fin).trim();
          String[] tokensCorchetes = dentro.split("[()=<>!+\\-*/\\s]+");
          //Validar el contenido de los corchetes
          for (String token: tokensCorchetes) {
            //Contenido a ignorar
            String contenidoToken = token.trim();
            if (contenidoToken.isEmpty()) continue;
            if (contenidoToken.matches("\\d+")) continue;
            if (palabrasIgnorar.contains(contenidoToken.toLowerCase())) continue;
            if (constantes.contains(contenidoToken)) continue;
            if (!identificadores.contains(contenidoToken)) {
              salida.printf("Error 235. Línea %04d. La variable '%s' en los corchetes no está declarada%n",
                numeroLinea, contenidoToken);
            }
          }
        }

        //Comprobar y validar el lado derecho
        ladoDer = ladoDer.replaceAll("[\\[\\];]", " ").trim();
        String[] tokens = ladoDer.split("[()=<>!+\\-*/\\s]+");
        for (String token: tokens) {
          //Contenido a ignorar
          String contenidoToken = token.trim();
          if (contenidoToken.isEmpty()) continue;
          if (contenidoToken.matches("\\d+")) continue;
          if (palabrasIgnorar.contains(contenidoToken.toLowerCase())) continue;
          if (constantes.contains(contenidoToken)) continue;
          if (!identificadores.contains(contenidoToken)) {
            salida.printf("Error 234. Línea %04d. La variable '%s' en la expresión no está declarada%n",
              numeroLinea, contenidoToken);
          }
        }
      }
    }
  }
  
  //Validacion de getdate, dec e inc
   public static void validarGetDate(String lineaTrim, String linea, int numeroLinea,
                                            Set<String> identificadores, PrintWriter salida) {

        if (lineaTrim.contains("dec") || lineaTrim.contains("getdate") || lineaTrim.contains("inc")) {
            int inicial = linea.indexOf("(");
            int fin = linea.lastIndexOf(")");

            if (inicial == -1 || fin == -1 || fin < inicial) {
                salida.printf("Error. Línea %04d. Se debe tener el paréntesis con una variable%n", numeroLinea);
            } else {
                String contenido = linea.substring(inicial + 1, fin).trim();
                String[] parametros = contenido.split(",");

                for (String parametro : parametros) {
                    String variable = parametro.trim();
                    boolean encontrado = false;

                    for (String id : identificadores) {
                        if (id.equalsIgnoreCase(variable)) {
                            encontrado = true;
                            break;
                        }
                    }

                    if (!encontrado) {
                        salida.printf("Error 233. Línea %04d. La variable '%s' no está declarada%n", numeroLinea, variable);
                    }
                }
            }
        }
    }
   
   //Validacion del if
   public static void validarIf(String lineaTrim, int numeroLinea,
                                 Set<String> identificadores, PrintWriter salida) {

        if (lineaTrim.startsWith("if")) {
            String condicion = lineaTrim.substring(2).trim();

            //Lista con palabras reservadas que no se validan como variables
            Set<String> palabrasIgnorar = new HashSet<>(Arrays.asList(
                    "and", "or", "not", "mod", "div", "then", "else"
            ));

            //Separar la condición por operadores y paréntesis
            String[] tokens = condicion.split("[()=<>!+\\-*/\\s]+");

            for (String token : tokens) {
                String variable = token.trim();
                if (variable.isEmpty()) continue;

                if (palabrasIgnorar.contains(variable.toLowerCase())) continue;
                if (variable.matches("\\d+")) continue; //Ignorar números

                List<String> contenidoCorchetes = new ArrayList<>();

                if (variable.contains("[")) {
                    int idxInicio = variable.indexOf("[");
                    int idxFin = variable.indexOf("]");
                    if (idxFin > idxInicio) {
                        String dentro = variable.substring(idxInicio + 1, idxFin).trim();
                        contenidoCorchetes.add(dentro);
                    }
                    variable = variable.substring(0, idxInicio).trim();
                }

                boolean encontrado = false;
                for (String id : identificadores) {
                    if (id.equalsIgnoreCase(variable)) {
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    salida.printf("Error 233. Línea %04d. La variable '%s' no está declarada%n",
                            numeroLinea, variable);
                }

                for (String contenido : contenidoCorchetes) {
                    boolean dentroValido = false;
                    for (String id : identificadores) {
                        if (id.equalsIgnoreCase(contenido)) {
                            dentroValido = true;
                            break;
                        }
                    }
                    if (!dentroValido) {
                        salida.printf("Error 233. Línea %04d. La variable '%s' dentro de corchetes no está declarada%n",
                                numeroLinea, contenido);
                    }
                }
            }
        }
    }
   //Validaciones del until y while
       public static void validarWhileUntil(String lineaTrim, int numeroLinea,
                                         Set<String> identificadores, PrintWriter salida) {

        if (lineaTrim.startsWith("while") || lineaTrim.startsWith("until")) {
            // Cortar la palabra clave ("while"/"until") y obtener la condición
            String condicion = lineaTrim.substring(5).trim();

            // Lista de palabras reservadas que no deben validarse
            Set<String> palabrasIgnorar = new HashSet<>(Arrays.asList(
                    "and", "or", "not", "mod", "div", "then", "else", "do"
            ));

            // Separar la condición por operadores, espacios o paréntesis
            String[] tokens = condicion.split("[()=<>!+\\-*/\\s]+");

            for (String token : tokens) {
                token = token.trim();
                if (token.isEmpty()
                        || palabrasIgnorar.contains(token.toLowerCase())
                        || token.matches("\\d+|\\d+;|;")) {
                    continue; // ignorar
                }

                // Tomar la parte antes del corchete
                String antes = token.contains("[")
                        ? token.substring(0, token.indexOf("[")).trim()
                        : token;

                if (!identificadores.contains(antes)) {
                    salida.printf("Error 233. Línea %04d. La variable '%s' no está declarada%n",
                            numeroLinea, antes);
                }

                // Validar contenido dentro de corchetes
                if (token.contains("[") && token.contains("]")) {
                    String dentro = token.substring(token.indexOf("[") + 1, token.indexOf("]")).trim();
                    if (!identificadores.contains(dentro)) {
                        salida.printf("Error 233. Línea %04d. La variable '%s' dentro de corchetes no está declarada%n",
                                numeroLinea, dentro);
                    }
                }
            }
        }
    }
}
