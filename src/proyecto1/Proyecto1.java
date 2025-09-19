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
      boolean esperandoCierre = false;
      Set < String > identificadores = new HashSet < > (); //Lista par añadir los identificadores
      Set < String > constantes = new HashSet < > (); //Lista para añadir las constantes

      //Mientras hallan líneas para leer
      while ((linea = entrada.readLine()) != null) {
        String lineaTrim = linea.trim().toLowerCase(); //Eliminar espacios sobrantes y convertir a minúscula 
        salida.printf("%04d %s%n", numeroLinea, linea); //Imprimir las líneas junto con su número de línea

        //Validaciones iniciales
        if (!programOriginal) {
          validacionesIniciales.validarProgram(lineaTrim, linea, numeroLinea, nombreArchivo, salida, programOriginal, esperandoUses);
          programOriginal = true;
          esperandoUses = true;
        } else if (esperandoUses) {
          validacionesIniciales.validarUses(lineaTrim, numeroLinea, salida, esperandoUses);
          esperandoUses = false;
        } else {
          validacionesIniciales.programRepetido(lineaTrim, numeroLinea, salida);
        }

        //validar los const
        if (lineaTrim.startsWith("uses")) {
          constCorrecto = true; //Se permiten const
        }

        if (lineaTrim.startsWith("var")) {
          constCorrecto = false; //ya no se permiten const
        }
        
        if (lineaTrim.startsWith("const")) {
          //Si todavía se esta esperando un cierre del const
          if (esperandoCierre) {
            salida.printf("Error 210. Línea %04d. El 'const' anterior no contiene un ';'%n", numeroLinea - 1);
            esperandoCierre = false; // se resetea para no encadenar más errores
          }

          if (!constCorrecto) {
            salida.printf("Error 234. Línea %04d. 'const' debe estar entre uses y var%n", numeroLinea);
          }

          if (!lineaTrim.endsWith(";")) {
            esperandoCierre = true;
          }

          validacionesConst.validarConst(lineaTrim, numeroLinea, salida, constCorrecto, constantes);
          constEncontrado = true;
        }
        //Si el const lleva multi línea
        if (esperandoCierre) {
          if (lineaTrim.endsWith(");")) {
            esperandoCierre = false;//Se cerro correctamente el const
          } else if (lineaTrim.startsWith("var")) {//El const no se ha cerrado y se encontro la palabra 'var'
            salida.printf("Error 212. Línea %04d. 'var' encontrado y 'const' no cerrado correctamente, falta ');'%n", numeroLinea);
            esperandoCierre = false;
          }
        }
        //Validaciones del var
        if (lineaTrim.startsWith("var")) {
          validacionesVar.validarVar(lineaTrim, numeroLinea, salida, esperandoUses, beginEncontrado, constEncontrado, identificadores);
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
          variableExiste.validarFor(lineaTrim, linea, numeroLinea, identificadores, constantes, salida);
        }

        if (lineaTrim.contains("dec") || lineaTrim.contains("getdate") || lineaTrim.contains("inc")) {
          variableExiste.validarGetDate(lineaTrim, linea, numeroLinea, identificadores, salida);
        }

        if (lineaTrim.startsWith("if")) {
          variableExiste.validarIf(lineaTrim, numeroLinea, identificadores, salida);
        }

        if (lineaTrim.startsWith("while") || lineaTrim.startsWith("until")) {
          variableExiste.validarUntilWhile(lineaTrim, numeroLinea, identificadores, salida);
        }

        //Validacion del end
        if (linea.startsWith("end")) {
          validacionesEnd.validarEnd(linea, lineaTrim, numeroLinea, salida, entrada, endEncontrado);
        }

        //Validaciones de los write y writeln
        if (lineaTrim.startsWith("write") || lineaTrim.startsWith("writeln")) {

          if (!beginEncontrado) {
            salida.printf("Error 216. Línea %04d. 'write' no puede estar antes de begin'%n", numeroLinea);
          }
          if (lineaTrim.startsWith("write")) {
            validacionesWrite.validarWrite(linea, lineaTrim, numeroLinea, salida,
              beginEncontrado, constantes, identificadores, entrada);
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