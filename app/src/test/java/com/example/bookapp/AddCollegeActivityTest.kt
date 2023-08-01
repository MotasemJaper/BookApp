import android.app.ProgressDialog
import com.example.bookapp.activities.AddCollegeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.base.CharMatcher.any
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class AddCollegeActivityTest {
    @Mock
    private val mockDatabase: FirebaseDatabase? = null

    @Mock
    private val mockReference: DatabaseReference? = null
    private var addCollegeActivity: AddCollegeActivity? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Mock the FirebaseDatabase and DatabaseReference
        `when`(mockDatabase!!.getReference("Colleges")).thenReturn(mockReference)
        `when`(mockReference!!.child(anyString())).thenReturn(mockReference)
        addCollegeActivity = AddCollegeActivity()
        addCollegeActivity!!.firebaseAuth = mock(FirebaseAuth::class.java)
        addCollegeActivity!!.progressDialog = mock(ProgressDialog::class.java)
        addCollegeActivity!!.college = "Test College"
    }

    @Test
    fun addCollageFirebase_Success() {
        // Mock FirebaseAuth and FirebaseUser
        val mockFirebaseAuth: FirebaseAuth = mock(FirebaseAuth::class.java)
        val mockFirebaseUser: FirebaseUser = mock(FirebaseUser::class.java)
        `when`(mockFirebaseUser.uid).thenReturn("testUserId")
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockFirebaseUser)
        addCollegeActivity!!.firebaseAuth = mockFirebaseAuth

        // Call the function
        val uri = "test_uri"
        addCollegeActivity!!.addCollageFirebase(uri)

        // Verify that the ProgressDialog is shown and dismissed
        verify(addCollegeActivity!!.progressDialog).show()
        verify(addCollegeActivity!!.progressDialog).dismiss()

        // Verify that the proper data is added to the DatabaseReference
        val hasMapCaptor: java.util.HashMap<*, *>? = ArgumentCaptor.forClass(HashMap::class.java).value
        verify(mockReference)!!.setValue(hasMapCaptor!!.capture())
        val capturedHasMap: HashMap<String, Any> = hasMapCaptor.getValue() as HashMap<String, Any>
        Assert.assertEquals("Test College", capturedHasMap["college"])
        Assert.assertEquals("test_uri", capturedHasMap["imageUri"])
        Assert.assertEquals("testUserId", capturedHasMap["uid"])
        Assert.assertNotNull(capturedHasMap["id"]) // We don't know the exact value, but it should not be null

        // Check if the 'timestamp' field is a valid Long value
        val timestampValue = capturedHasMap["timestamp"]
        Assert.assertTrue(timestampValue is Long)
        // Check if the Toast message is displayed correctly
        verify(addCollegeActivity)!!.showToast("Added successfully...")
    }

    @Test
    fun addCollageFirebase_Failure() {
        // Mock the failure case for DatabaseReference setValue
        `when`(mockReference!!.setValue(any())).thenThrow(RuntimeException("Test error"))

        // Call the function
        val uri = "test_uri"
        addCollegeActivity!!.addCollageFirebase(uri)

        // Verify that the ProgressDialog is shown and dismissed
        verify(addCollegeActivity!!.progressDialog).show()
        verify(addCollegeActivity!!.progressDialog).dismiss()

        // Check if the Toast message is displayed correctly with the error message
        verify(addCollegeActivity)!!.showToast("Test error.")
    }


}

private fun <K, V> HashMap<K, V>.getValue(): Boolean
{
return true
}

private fun <K, V> java.util.HashMap<K, V>.capture(): V? {
    TODO("Not yet implemented")
}
