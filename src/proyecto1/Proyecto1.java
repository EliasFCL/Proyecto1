//Leer desde el cmd: https://www.delftstack.com/howto/java/args-java/?utm
//Crear un archivo con mismo contenido: https://www.tutorialspoint.com/java/java_files_io.htm
//Uso de printf: https://www.it.uc3m.es/pbasanta/asng/course_notes/input_output_printf_es.html
//Determinar cuando se cierra una comilla simple: https://es.stackoverflow.com/questions/137583/determinar-cuantas-vocales-tiene-una-cadena-en-java
//Tokens: https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes y https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
package proyecto1;

import java.io.*;
import java.util.*;

public class Proyecto1 {
  public static void main(String[] args) {

    //Verificar en el cmd que se este ingresando el nombre del archivo
    if (args.length == 0) {
      System.out.println("Por favor indique el nombre del archivo con la extensión .pas");
      return;
    }

    String entradaCMD = args[0]; //Obtener el archivo .pas
    String nombreArchivo = entradaCMD.replace(".pas", ""); //Reemplazar el .pas para solo tener el nombre del archivo
    String archivoErrores = nombreArchivo + "-errores.err";

    try (
      BufferedReader entrada = new BufferedReader(new FileReader(entradaCMD)); //Leer el archivo indicado en el cmd
      PrintWriter salida = new PrintWriter(new FileWriter(archivoErrores)) //Crear el archivo de errores
    ) {
      //Variables de control
      String linea;
      int numeroLinea = 1;
      boolean programOriginal = false;
      boolean esperandoUses = false;
      boolean constCorrecto = false;
      boolean constEncontrado = false;
      boolean despuesVar = false;
      boolean endEncontrado = false;
      boolean beginEncontrado = false;
      Set < String > identificadores = new HashSet < > (); //Lista par añadir los identificadores
      Set < String > constantes = new HashSet < > ();

      //Mientras hallan líneas para leer
      while ((linea = entrada.readLine()) != null) {
        String lineaTrim = linea.trim().toLowerCase(); //Eliminar espacios sobrantes y convertir a minúscula 
        salida.printf("%04d %s%n", numeroLinea, linea); //Imprimir las líneas junto con su número

        if (!programOriginal) {

          //Verificar que no haya contenido antes de program
          if (lineaTrim.startsWith("//") || lineaTrim.startsWith("{") || linea.isEmpty()) {
            salida.printf("Error 204. Línea %04d. No pueden haber líneas, espacios o comentarios antes de 'program'%n", numeroLinea);
          }

          //Verificar que el archivo .pas inicie con program
          if (!lineaTrim.startsWith("program")) {
            salida.printf("Error 204. Línea %04d. Debe comenzar con 'program'%n", numeroLinea);
          }

          //Comprobar que termine en punto y coma
          if (!lineaTrim.endsWith(";")) {
            salida.printf("Error 202. Línea %04d. Falta punto y coma al final%n", numeroLinea);
          }

          //Comprobar el nombre después de 'program'
          String nombreEnLinea = lineaTrim.substring(7).replace(";", "").trim();
          if (!nombreEnLinea.equalsIgnoreCase(nombreArchivo)) {
            salida.printf("Error 201. Línea %04d. El nombre '%s' no coincide con el archivo '%s'%n",
              numeroLinea, nombreEnLinea, nombreArchivo);
          }

          programOriginal = true;
          esperandoUses = true;

        } else if (esperandoUses) {
          if (lineaTrim.isEmpty() || lineaTrim.startsWith("//") || lineaTrim.startsWith("{")) {
            salida.printf("Error 205. Línea %04d. No puede haber nada entre 'program' y 'uses'%n", numeroLinea);
            break;
          }
          //Verificar que exista uses despues de program
          if (!lineaTrim.startsWith("uses")) {
            salida.printf("Error 206. Línea %04d. Debe aparecer 'uses' después de 'program'%n", numeroLinea);
          }

          if (!lineaTrim.endsWith(";")) {
            salida.printf("Error 207. Línea %04d. La línea 'uses' debe terminar en punto y coma%n", numeroLinea);
          }

          esperandoUses = false; //Ya se encontro el uses 
          if (!esperandoUses) {

          }
        } else {
          //Verificar que no se repita "program"
          for (String palabra: lineaTrim.split("[^a-zA-Z0-9_]+")) {
            if (palabra.equalsIgnoreCase("program")) {
              salida.printf("Error 203. Línea %04d. 'program' está repetido%n", numeroLinea);
              break;
            }
          }
        }

        //validar los const
        if (lineaTrim.startsWith("uses")) {
          constCorrecto = true; //Se permiten const
        }

        if (lineaTrim.startsWith("var")) {
          constCorrecto = false; //ya no se permiten const
        }
        if (lineaTrim.startsWith("const")) {

          if (!constCorrecto) {
            salida.printf("Error 210. Línea %04d. 'const' debe estar entre uses y var%n", numeroLinea);
          }
          if (!lineaTrim.contains("=")) {
            salida.printf("Error 210. Línea %04d. 'const' debe contener un '=' despues del identificador%n", numeroLinea);
          }
          if (!lineaTrim.endsWith(";")) {
            salida.printf("Error 210. Línea %04d. 'const' debe contener un ';' al final%n", numeroLinea);
          }
          // Quitar la palabra 'const'
          String declaracion = lineaTrim.substring(5).trim();

          // Dividir solo por coma **entre constantes**, pero no dentro de los valores
          String[] partes = declaracion.split("\\s*,\\s*(?=[a-zA-Z])"); // separa solo cuando empieza con letra

          for (String parte: partes) {
            parte = parte.trim();

            // Guardar solo lo que está antes del '=' y ':' 
            String nombreConstante = parte.split("=")[0].split(":")[0].trim();
            if (!nombreConstante.isEmpty()) {
              constantes.add(nombreConstante);
            }
          }

          constEncontrado = true;
        }
        //Validaciones del var
        if (lineaTrim.startsWith("var")) {
          //Verificar si 'var' aparece después de 'uses' y antes de 'begin'
          if (esperandoUses || beginEncontrado || !constEncontrado) {
            salida.printf("Error 211. Línea %04d. 'var' debe aparecer después de 'const' y antes de 'begin'%n", numeroLinea);
          }
          //Lista de palabras reservadas de Pascal
          Set < String > reservadas = new HashSet < > (Arrays.asList(
            "ABSOLUTE", "DOWNTO", "BEGIN", "DESTRUCTOR", "MOD", "AND", "ELSE", "CASE", "EXTERNAL", "NOT",
            "ARRAY", "END", "CONST", "DIV", "PACKED", "ASM", "FILE", "CONSTRUCTOR", "DO", "PROCEDURE", "FOR",
            "FORWARD", "FUNCTION", "GOTO", "RECORD", "IF", "IN", "OR", "PRIVATE", "UNTIL", "PROGRAM", "REPEAT",
            "STRING", "THEN", "VAR", "WHILE", "XOR", "WITH", "TYPE", "OF", "USES", "SET", "OBJECT", "TO"
          ));
          //Quitar "var" y los espacios al principio
          String declaracion = lineaTrim.substring(3).trim();

          //Separar las variables por coma
          String[] partes = declaracion.split(",");

          //Procesar cada variable
          for (String parte: partes) {
            parte = parte.trim();
            //Tomar el identificador antes del ':'
            String identificador = parte.split(":")[0].trim();
            identificadores.add(identificador); //Añadir los identificadores encontrados
            if (reservadas.contains(identificador.toUpperCase())) {
              salida.printf("Error 226. Línea %04d. Se esta utilizando una palabra reservada para declarar un identificador%n", numeroLinea);
            }
            //Buscar los dos puntos en la línea
            if (lineaTrim.contains(":")) {
              String espacio = lineaTrim.substring(lineaTrim.indexOf(":") + 1);

              //Revisar si empieza con espacio
              if (!espacio.startsWith(" ")) {
                salida.printf("Error. Línea %04d. Debe haber un espacio después de los dos puntos en la declaración de la variable.%n", numeroLinea);
              }
            }
            //Verificar que el nombre de la variable empiece con una letra
            if (!Character.isLetter(identificador.charAt(0))) {
              salida.printf("Error. Línea %04d. El nombre de la variable '%s' debe comenzar con una letra.%n", numeroLinea, identificador);
            } else

              //Verificar que el identificador contenga solo letras y guiones bajos, junto con el punto y coma
              if (!identificador.matches("^[a-zA-Z][a-zA-Z_]*$")) {
                salida.printf("Error. Línea %04d. El identificador '%s' es inválido. Usar solo letras y guiones bajos.%n", numeroLinea, identificador);
              }

            String despuesDosPuntos = lineaTrim.substring(lineaTrim.indexOf(":") + 1).trim();

            //Quitar el punto y coma final
            if (despuesDosPuntos.endsWith(";")) {
              despuesDosPuntos = despuesDosPuntos.substring(0, despuesDosPuntos.length() - 1).trim();
            }

            //Si hay "of", quedarse con lo último
            if (despuesDosPuntos.toLowerCase().contains("of")) {
              despuesDosPuntos = despuesDosPuntos.substring(despuesDosPuntos.toLowerCase().lastIndexOf("of") + 2).trim();
            }

            //Validar que sea uno de los tipos permitidos
            if (!(despuesDosPuntos.equalsIgnoreCase("integer") ||
                despuesDosPuntos.equalsIgnoreCase("string") ||
                despuesDosPuntos.equalsIgnoreCase("word"))) {
              salida.printf("Error. Línea %04d. Tipo de variable inválido: %s%n", numeroLinea, despuesDosPuntos);
            } else if (!lineaTrim.endsWith(";")) {
              salida.printf("Error. Línea %04d. La declaracion del var debe terminar con punto y coma.%n", numeroLinea, despuesDosPuntos);
            }
          }
        }

        //Validaciones del begin
        if (lineaTrim.startsWith("var")) {
          despuesVar = true; //Marcar que ya se encontraron declaraciones var
        }

        if (lineaTrim.startsWith("begin")) {
          //Validar que aparezca después de var
          if (!despuesVar) {
            salida.printf("Error 211. Línea %04d. 'begin' debe aparecer después de la declaración de var%n", numeroLinea);
          }

          //Validar que begin esté solo en su línea
          if (!lineaTrim.equals("begin")) {
            salida.printf("Error 212. Línea %04d. 'begin' debe estar solo en su línea, sin otras palabras ni comentarios%n", numeroLinea);
          }
          beginEncontrado = true;
        }
        //Validar que existan las variables
        if (lineaTrim.startsWith("for") || lineaTrim.contains(":=")) {
          if (lineaTrim.startsWith("for")) {
            String[] tokens = lineaTrim.split("\\s+"); //Separar por espacios
            if (tokens.length < 2) {
              salida.printf("Error 233. Línea %04d. Despues del for debe haber un identificador%n", numeroLinea);
            } else {
              String varFor = tokens[1]; //La variable que sigue a 'for'
              //Extrar solo lo que esta antes del ":"
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
          } else
          if (lineaTrim.contains(":=")) {
            String[] tokens = lineaTrim.split("\\s+");
            String varFor = tokens[1]; //La variable que esta antes de ':='

            boolean encontrado = false;

            //Comparar uno por uno con los identificadores declarados
            for (String id: identificadores) {
              if (lineaTrim.startsWith(id)) {
                encontrado = true;
                break; //Variable encontrada
              }
            }

            if (!encontrado) {
              salida.printf("Error 233. Línea %04d. La variable inicial no está declarada o no existe%n", numeroLinea, varFor);
            }
          }
        }
        if (lineaTrim.contains("dec") || lineaTrim.contains("getdate") || lineaTrim.contains("inc")) {
          //Ubicar posiciones de los parentésis
          int inicial = linea.indexOf("(");
          int fin = linea.lastIndexOf(")");

          //Validar que esten en el orden correcto
          if (inicial == -1 || fin == -1 || fin < inicial) {
            salida.printf("Error. Línea %04d. Se debe tener el paréntesis con una variable%n", numeroLinea);
          } else {
            String contenido = linea.substring(inicial + 1, fin).trim();//Extraer el contenido del parentésis
            String[] parametros = contenido.split(",");//Separar por comas

            for (String parametro: parametros) {
              String variable = parametro.trim();//Quitar espacios antes y después
              boolean encontrado = false;

              //Comparar uno por uno con los identificadores declarados
              for (String id: identificadores) {
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
        if (lineaTrim.startsWith("if")) {
          String condicion = lineaTrim.substring(2).trim();
          //Lista con palabras reservadas
          Set < String > palabrasIgnorar = new HashSet < > (Arrays.asList(
            "and", "or", "not", "mod", "div", "then", "else"
          ));

          //Separar por operadores y paréntesis
          String[] tokens = condicion.split("[()=<>!+\\-*/\\s]+");

          //Recorrer cada token de la línea
          for (String token: tokens) {
            String variable = token.trim();
            if (variable.isEmpty()) continue;

            // Ignorar palabras reservadas
            if (palabrasIgnorar.contains(variable.toLowerCase())) continue;

            // Ignorar números
            if (variable.matches("\\d+")) continue;

            // Si tiene corchetes, quedarse con la parte antes del corchete
            if (variable.contains("[")) {
              variable = variable.substring(0, variable.indexOf("[")).trim();
            }

            boolean encontrado = false;
            //Comparar variable por varible
            for (String id: identificadores) {
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
        if (lineaTrim.startsWith("while") || lineaTrim.startsWith("until")) {
          String condicion = lineaTrim.substring(5).trim();
          //Lista con palabras reservadas
          Set < String > palabrasIgnorar = new HashSet < > (Arrays.asList(
            "and", "or", "not", "mod", "div", "then", "else", "do"
          ));

          //Separar por operadores y paréntesis
          String[] tokens = condicion.split("[()=<>!+\\-*/\\s]+");

          //Recorrer cada token de la línea
          for (String token: tokens) {
            String variable = token.trim();
            if (variable.isEmpty()) continue;

            //Ignorar palabras reservadas
            if (palabrasIgnorar.contains(variable.toLowerCase())) continue;

            //Ignorar números
            if (variable.matches("\\d+") || variable.matches("\\d+" + ";") || variable.matches(";")) continue;
            //Si tiene conrchetes, quedarse con la parte antes del corchete
            if (variable.contains("[")) {
              variable = variable.substring(0, variable.indexOf("[")).trim();
            }
            boolean encontrado = false;
            //Comparar variable por varible
            for (String id: identificadores) {
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
        //Validaciones del end
        if (linea.startsWith("end")) {
          if (lineaTrim.equals("end.")) {

            endEncontrado = true; //Es el final correcto

            // Verificar que no tenga comentarios
            if (linea.contains("//") || linea.contains("{")) {
              salida.printf("Error 213. Línea %04d. No se permiten comentarios en la línea de 'end.'%n", numeroLinea);
            }
          } else {
            //Comprobar que sea especificamente "end."
            salida.printf("Error 214. Línea %04d. el end final solo puede aparecer de la siguiente manera 'end.' más%n", numeroLinea);
            endEncontrado = true;
          }
        } else if (endEncontrado) {
          //Verificar que despues del end. no haya nada
          salida.printf("Error 215. Línea %04d. No puede haber líneas después de 'end.'%n", numeroLinea);
        }

        //Validaciones de los write y writeln
        if (lineaTrim.startsWith("write") || lineaTrim.startsWith("writeln")) {

          if (!beginEncontrado) {
            salida.printf("Error 216. Línea %04d. 'write' no puede estar antes de begin'%n", numeroLinea);
          }

          int inicio = linea.indexOf("(");
          int fin = linea.lastIndexOf(")");
          //Validar los parentésis
          if (inicio == -1 || fin == -1 || fin <= inicio) {
            salida.printf("Error 217. Línea %04d. la línea del 'write' no contiene uno de sus paréntesis%n", numeroLinea);
          } else {
            String contenido = linea.substring(inicio + 1, fin).trim();
            if (contenido.isEmpty()) {
              salida.printf("Error 219. Línea %04d. los paréntesis no pueden ir vacíos%n", numeroLinea);
            } else {
              boolean dentroComilla = false;
              boolean errorCadena = false;
              //Comprobar si las comillas estan bien cerrardas
              for (int i = 0; i < contenido.length(); i++) {
                char caracter = contenido.charAt(i);
                if (caracter == '\'') {
                  dentroComilla = !dentroComilla;
                  if (!dentroComilla) errorCadena = false;
                } else if (dentroComilla && caracter == ',' && !errorCadena) {
                  salida.printf("Error 220. Línea %04d. falta comilla antes de la coma%n", numeroLinea);
                  errorCadena = true;
                }
              }
              if (dentroComilla && !errorCadena) {
                salida.printf("Error 221. Línea %04d. falta cerrar comilla en la cadena del paréntesis%n", numeroLinea);
              }
            }

            // Validación de variables y constantes
            String[] tokens = contenido.split(",");
            Set < String > palabrasIgnorar = new HashSet < > (Arrays.asList(
              "and", "or", "not", "mod", "div", "then", "else", "do"
            ));
            
            for (String token: tokens) {
              //Contenido a ignorar
              String contenidoToken = token.trim();
              if (contenidoToken.isEmpty()) continue;
              if (palabrasIgnorar.contains(contenidoToken.toLowerCase())) continue;
              if (contenidoToken.matches("('.*'|#\\d+|'[^']*'#\\d+|#\\d+'.*')")) continue;
              if (contenidoToken.matches("((#\\d+)|('.*?'))+")) continue;
              if (constantes.contains(contenidoToken)) continue;
              if (contenidoToken.matches("\\d+") || contenidoToken.matches("#"+"\\d+") || contenidoToken.equals(";")) continue;

              //Variables dentro corchetes
              String corchete = contenidoToken;
              String contCorchete = null;
              if (contenidoToken.contains("[")) {
                corchete = contenidoToken.substring(0, contenidoToken.indexOf("[")).trim();
                contCorchete = contenidoToken.substring(contenidoToken.indexOf("[") + 1, contenidoToken.indexOf("]")).trim();
              }

              //Quitar los corchetes 
              corchete = corchete.replaceAll(":\\d+$", "");

              boolean encontrado = false;
              //Revisar si estan los corchetes
              if (identificadores.contains(corchete) || constantes.contains(corchete)) {
                encontrado = true;
              }

              //Revisar contenido de los corchetes
              if (contCorchete != null && !identificadores.contains(contCorchete)) {
                encontrado = false;
              }

              if (!encontrado) {
                salida.printf("Error 233. Línea %04d. El token '%s' no es una variable o constante válida%n", numeroLinea, contenidoToken);
              }
            }
          }
          
          if (!lineaTrim.endsWith(";")) {
            salida.printf("Error 222. Línea %04d. falta el punto y coma del write%n", numeroLinea);
          }
        }
        //Validaciones de comentarios
        if (lineaTrim.startsWith("/")) {
          if (!lineaTrim.startsWith("//")) {
            salida.printf("Error 223. Línea %04d. Para iniciar un comentario debe usar '//'%n", numeroLinea);
          }
        }
        if (lineaTrim.startsWith("{")) {
          if (!lineaTrim.endsWith("}")) {
            salida.printf("Error 224. Línea %04d. Falta el segundo corchete, no usar comentarios de doble línea %n", numeroLinea);
          }
        } else if (lineaTrim.startsWith("}")) {
          salida.printf("Error 225. Línea %04d. Ese corchete es invalido debe empezar con '{'%n", numeroLinea);
        }
        //Validar que no haya contenido después del punto y coma
        if (lineaTrim.contains(";")) {
          int postPuntoComa = lineaTrim.indexOf(";");
          String despuesPuntoComa = lineaTrim.substring(postPuntoComa + 1).trim();
          if (!despuesPuntoComa.isEmpty()) {
            salida.printf("Error 225. Línea %04d. No puede haber ningun tipo de contenido después del ';'%n", numeroLinea);
          }
        }

        numeroLinea++; //Aumentar el número de líneas
      }

      System.out.println("Archivo de errores creado: " + archivoErrores);

    } catch (IOException e) {
      System.err.println("Error al leer o escribir el archivo: " + e.getMessage());
    }
  }
}