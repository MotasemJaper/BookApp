import com.example.bookapp.activities.AddMajorActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.stubbing.OngoingStubbing

class AddMajorActivityTest {

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var firebaseDatabase: FirebaseDatabase

    @Mock
    private lateinit var mockAuthResult: Task<AuthResult>

    @Mock
    private lateinit var mockDatabaseReference: DatabaseReference

    private lateinit var addMajorActivity: AddMajorActivity

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        addMajorActivity = AddMajorActivity()
        // Set up the necessary dependencies (e.g., firebaseAuth, firebaseDatabase) in the addMajorActivity
    }

    @After
    fun tearDown() {
        // Perform any cleanup after the test is done.
    }

    @Test
    fun testAddMajor_Success() {
        // Mock any required values
        val selectedCollegeTitle = "Sample College Title"
        val selectedCollegeId = "SampleCollegeId"
        val majorName = "Sample Major Name"
        Mockito.`when`(firebaseAuth.uid).thenReturn("SampleUid")
        Mockito.`when`(firebaseDatabase.getReference("Majors")).thenReturn(mockDatabaseReference)
        Mockito.`when`(mockDatabaseReference.child(selectedCollegeTitle)).thenReturn(mockDatabaseReference)
        // Execute the function
        addMajorActivity.addMajor()
        // Add assertions to check if the function behaves as expected after successful completion.
    }

    @Test
    fun testAddMajor_Failure() {
        // Mock any required values and set up the necessary mocks for failure case.
        val selectedCollegeTitle = "Sample College Title"
        val selectedCollegeId = "SampleCollegeId"
        val majorName = "Sample Major Name"
        Mockito.`when`(firebaseAuth.uid).thenReturn("SampleUid")
        Mockito.`when`(firebaseDatabase.getReference("Majors")).thenReturn(mockDatabaseReference)
        Mockito.`when`(mockDatabaseReference.child(selectedCollegeTitle)).thenReturn(mockDatabaseReference)
        // Mock the failure case for setValue method
        Mockito.`when`(mockDatabaseReference.child("").setValue("hashMap"))
            .thenReturn("mockTaskFailure")

        // Execute the function
        addMajorActivity.addMajor()

        // Add assertions to check if the function behaves as expected after a failure case.
    }
}

private fun <T> OngoingStubbing<T>.thenReturn(s: String) {

}
