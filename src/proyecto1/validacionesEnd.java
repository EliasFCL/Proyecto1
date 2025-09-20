package proyecto1;

import java.io.*;
/**
 *
 * @author Dell
 */
public class validacionesEnd {

  public static void validarEnd(String linea, String lineaTrim, int numeroLinea, PrintWriter salida, BufferedReader entrada, boolean endEncontrado) {
    try {
      if (!endEncontrado) {
        if (lineaTrim.equals("end.")) {

          endEncontrado = true; //Es el End final

          //Verificar que no tenga comentarios
          if (linea.contains("//") || linea.contains("{")) {
            salida.printf("Error 030. Línea %04d. No se permiten comentarios en la línea de 'end.'%n", numeroLinea);
          }
          // Verificar que no haya líneas con contenido después del end.
          String siguienteLinea = entrada.readLine();
          if (siguienteLinea != null && !siguienteLinea.trim().isEmpty()) {
            salida.printf("Error 031. Línea %04d. No puede haber líneas después de 'end.'%n", numeroLinea);
          }
        } else {
          //Si no es "end." mostrar error
          salida.printf("Error 032. Línea %04d. El end final solo puede ser 'end.' sin nada en su línea'%n", numeroLinea);
          endEncontrado = true;
        }
      }
    } catch (Exception e) {
      salida.printf("Error inesperado en validación de 'end' en la línea %04d: %s%n", numeroLinea, e.getMessage());
    }
  }
}