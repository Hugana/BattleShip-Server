package ICD;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Base64;

@WebServlet("/UploadPhotoServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
        maxFileSize = 1024 * 1024 * 50, // 50 MB
        maxRequestSize = 1024 * 1024 * 100)
public class UploadPhotoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public UploadPhotoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");

        HttpSession session = request.getSession();

        if (request.getMethod().equalsIgnoreCase("post")) {
            Jogador JW = (Jogador) session.getAttribute("Jogador");

            if (JW.changePictureServlet()) {

                Part filePart = request.getPart("picture");

                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

                System.out.println(fileName);
                System.out.println(getExtension(fileName));
                System.out.println(getMimeType(fileName));

                InputStream fileContent = filePart.getInputStream();

                String fileBase64 = convertToBase64(fileContent);

                JW.Write("data:" + getMimeType(fileName) + ";base64," + fileBase64);

                session.setAttribute("photoFileName", fileName);
                session.setAttribute("photoBase64", fileBase64);

                response.sendRedirect("uploaded_photo.jsp");
            }
        }
    }

    private String convertToBase64(InputStream inputStream) throws IOException {
        byte[] fileContent = inputStream.readAllBytes();
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private static final String getMimeType(String path) {
        switch (getExtension(path)) {
            case "txt":
                return "text/plain";
            case "gif":
                return "image/gif";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "bmp":
                return "image/bmp";
            case "png":
                return "image/png";
            default:
                return "application/octet-stream";
        }
    }

    private static String getExtension(String path) {
        int dot = path.lastIndexOf(".");
        if (dot == -1)
            return "";
        return path.substring(dot + 1);
    }
}
