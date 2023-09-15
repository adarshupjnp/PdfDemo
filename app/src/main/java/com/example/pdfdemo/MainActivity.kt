package com.example.pdfdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pdfdemo.databinding.ActivityMainBinding
import com.itextpdf.text.Document
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.OutputStream

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private val WRITE_EXTERNAL_STORAGE_REQUEST = 1
    private val CREATE_PDF_REQUEST = 2

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.download.setOnClickListener(this)

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.download -> {
                createPDF()
            }
        }
    }

    // Function to check and request permission.
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@MainActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createPDF() {
        val document = Document(PageSize.A4)

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "sample.pdf")
        }

        startActivityForResult(intent, CREATE_PDF_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CREATE_PDF_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                contentResolver.openOutputStream(uri)?.let { outputStream ->
                    generatePdfContent(outputStream)
                }
            }
        }
    }

    private fun generatePdfContent(outputStream: OutputStream) {
        val document = Document(PageSize.A4)

        try {
            PdfWriter.getInstance(document, outputStream)
            document.open()

            // Add content to the PDF
            document.add(Paragraph(" Name : Adarsh \n Position : Android Developer \n Company Name : Xceltec Pvt Ltd \n Location : Ahmedabad"))
            document.addCreationDate()
            document.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            document.close()
        }
    }

}
