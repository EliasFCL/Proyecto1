package proyecto1;

import java.io.*;
/**
 *
 * @author Dell
 */
public class validacionesEnd {

  public static void validarEnd(String lineaTrim, int numeroLinea, PrintWriter salida, BufferedReader entrada) {
    try {
        if (lineaTrim.equals("end.")) {
            
          //Verificar que no tenga comentarios
          if (lineaTrim.contains("//") || lineaTrim.contains("{")) {
            salida.printf("Error 030. Línea %04d. No se permiten comentarios en la línea de 'end.'%n", numeroLinea);
          }
          //Verificar que no haya líneas con contenido después del end.
          String siguienteLinea = entrada.readLine();
          if (siguienteLinea.trim() != null) {
            salida.printf("Error 031. Línea %04d. No puede haber líneas después de 'end.'%n", numeroLinea);
          }
        } else {
          //Si no es "end." mostrar error
          salida.printf("Error 032. Línea %04d. El end final solo puede ser 'end.' sin nada en su línea'%n", numeroLinea);
        }
    } catch (Exception e) {
      //Solo capturar el error cuando la siguiente línea es nula
    }
  }
}