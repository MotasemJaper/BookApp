package com.example.bookapp
import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import com.example.bookapp.activities.AddCollegeActivity
import com.example.bookapp.activities.DetailBookActivity
import com.example.bookapp.activities.EditMajorActivity
import com.example.bookapp.activities.Model.ModelPdf
import com.example.bookapp.activities.PdfViewActivity
import com.example.bookapp.activities.adapter.AdapterViewCourse
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyMapOf
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.stubbing.OngoingStubbing

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    private lateinit var addCollegeActivity: AddCollegeActivity
    private lateinit var editMajorActivity: EditMajorActivity

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var progressDialog: ProgressDialog

    @Mock
    private lateinit var databaseReference: DatabaseReference

    @Mock
    private lateinit var removeTask: Task<Void>

    private lateinit var pdfView: PdfViewActivity

    @Mock
    private lateinit var database: FirebaseDatabase

    @Mock

    private lateinit var snapshot: DataSnapshot

    @Mock
    private lateinit var listener: PdfViewActivity
    @Before
    fun setup() {
        addCollegeActivity = AddCollegeActivity()
        editMajorActivity = EditMajorActivity()
        MockitoAnnotations.initMocks(this)
        `when`(FirebaseDatabase.getInstance()).thenReturn(mock(FirebaseDatabase::class.java))
        `when`(FirebaseDatabase.getInstance().getReference("Courses")).thenReturn(databaseReference)
        `when`(databaseReference.child("Sample Course")).thenReturn(databaseReference)
        `when`(databaseReference.removeValue()).thenReturn(removeTask)
        `when`(progressDialog.isShowing).thenReturn(true, false)

        `when`(database.getReference("Pdf")).thenReturn(databaseReference)
        `when`(databaseReference.child(any())).thenReturn(databaseReference)
        `when`(databaseReference.orderByChild(any())).thenReturn(databaseReference)
        `when`(snapshot.children).thenReturn(emptyList())

    }

    @Test
    fun addToFavorite_Success() {
        // Mock the behavior of the DatabaseReference
        val databaseReferenceChild = mock(DatabaseReference::class.java)
        `when`(databaseReference.child(anyString())).thenReturn(databaseReferenceChild)
        val setValueTask = mock(Task::class.java)
        `when`(databaseReferenceChild.setValue(any())).thenReturn(setValueTask as Task<Void>?)
        `when`(setValueTask.addOnSuccessListener(any())).thenAnswer {
            val onSuccessListener = it.getArgument<OnSuccessListener<Any>>(0)
            onSuccessListener.onSuccess(null)
        }
        val details = DetailBookActivity()
        details.addToFavorite()

    }
    @Test
    fun addToFavorite_Failure() {
        // Mock the behavior of the DatabaseReference
        val databaseReferenceChild = mock(DatabaseReference::class.java)
        `when`(databaseReference.child(anyString())).thenReturn(databaseReferenceChild)
        val setValueTask = mock(Task::class.java)
        `when`(databaseReferenceChild.setValue(any())).thenReturn(setValueTask as Task<Void>?)
        `when`(setValueTask.addOnFailureListener(any())).thenAnswer {
            val onFailureListener = it.getArgument<OnFailureListener>(0)
            onFailureListener.onFailure(Exception("Failed to add to favorites"))
        }
        val details = DetailBookActivity()
        details.addToFavorite()

    }
    @Test
    fun loadExamPdfView_Success() {
        val pdfList = listOf(ModelPdf(), ModelPdf())
        `when`(snapshot.children).thenReturn(pdfList.map { mockDataSnapshot(it) })
        `when`(listener.loadBookView(any())).then {
            val loadedPdfList = it.getArgument<List<ModelPdf>>(0)
            assert(loadedPdfList.size == 2)
        }
        pdfView.loadBookView("1691216185374")
    }
    @Test
    fun loadExamPdfView_Failure() {
        val errorMessage = "Loaded Failure"
        `when`(listener.loadBookView(any())).then {
            val error = it.getArgument<String>(0)
            assert(error == errorMessage)
        }
        pdfView.loadBookView("1691216185374")
    }
    private fun mockDataSnapshot(model: ModelPdf): DataSnapshot {
        val snapshot: DataSnapshot = mock()
        `when`(snapshot.getValue(ModelPdf::class.java)).thenReturn(model)
        return snapshot
    }

    private fun mock(): DataSnapshot {
        TODO("Not yet implemented")
    }

    @Test
    fun deleteCourse_Success() {
        val courseName = "Java 2"
        val id = "1691216185374"
        val course = AdapterViewCourse(context, ArrayList())
        course.deleteCourse(courseName, id)
        verify(progressDialog).show()
        verify(progressDialog).dismiss()
        Toast.makeText(context, "Deleted!!", Toast.LENGTH_SHORT).show()
    }
    @Test
    fun deleteCourse_Failure() {
        `when`(removeTask.isSuccessful).thenReturn(false)
        val courseName = "Java 2"
        val id = "1691216185374"
        val course = AdapterViewCourse(context, ArrayList())
        course.deleteCourse(courseName, id)
        verify(progressDialog).show()
        verify(progressDialog).dismiss()
        Toast.makeText(context, "Error message", Toast.LENGTH_SHORT).show()
    }

    @Test
    fun updateMajorName_Success() {
        val progressDialog = mock(ProgressDialog::class.java)
        val binding = mock(EditMajorActivity::class.java)
        val ref = mock(DatabaseReference::class.java)

        `when`(binding.binding).thenReturn("Mobile Developer")

        val updateTask = mock(Task::class.java) as Task<Void>

        `when`(ref.updateChildren(anyMapOf(String::class.java, Any::class.java)))
            .thenReturn(updateTask)
//
        editMajorActivity.progressDialog = progressDialog
        editMajorActivity.updateMajorName()

        verify(progressDialog).show()
        verify(progressDialog).dismiss()
        verify(binding.applicationContext).equals("Updated")
    }

    @Test
    fun updateMajorName_Failure() {

    }
    @Test
    fun testValidateData_CollegeEmpty() {
        val college = ""
        addCollegeActivity.validateData()
        verify(addCollegeActivity).runOnUiThread {
            Toast.makeText(addCollegeActivity, "Enter College...", Toast.LENGTH_SHORT).show()
        }
        verify(addCollegeActivity, never()).addCollageFirebase(anyString())
    }
    @Test
    fun testValidateData_CollegeNotEmpty() {
        val college = "Test College"
        addCollegeActivity.validateData()
        verify(addCollegeActivity).addCollageFirebase(anyString())
        verify(addCollegeActivity, never()).uploadImageForCollege()
    }
}

private fun <T> OngoingStubbing<T>.thenReturn(s: String) {


}








