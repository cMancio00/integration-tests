package school.cotroller;

import static org.mockito.Mockito.*;
import static java.util.Arrays.asList;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import school.model.Student;
import school.repository.StudentRepository;
import school.view.StudentView;

public class SchoolControllerTest {

	@Mock
	private StudentRepository studentRepository;
	@Mock
	private StudentView studentView;

	@InjectMocks
	private SchoolController schoolController;

	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAllStudents() {
		List<Student> students = asList(new Student("1", "test"));
		when(studentRepository.findAll()).thenReturn(students);
		schoolController.allStudents();
		verify(studentView).showAllStudents(students);
	}

	@Test
	public void testNewStudentAddedWhenStudentDoesNotAlreadyExists() {
		Student student = new Student("1", "test");
		when(studentRepository.findById("1")).thenReturn(null);
		schoolController.newStudent(student);
		InOrder inOrder = inOrder(studentRepository, studentView);
		inOrder.verify(studentRepository).save(student);
		inOrder.verify(studentView).studentAdded(student);
	}

	@Test
	public void testNewStudentAddedWhenAlreadyExisting() {
		Student existingStudent = new Student("1", "existing");
		Student toAdd = new Student("1", "test");
		when(studentRepository.findById("1")).thenReturn(existingStudent);
		schoolController.newStudent(toAdd);
		verify(studentView).showError("Existing studend with id 1", existingStudent);
		verifyNoMoreInteractions(ignoreStubs(studentRepository));
	}
	
	@Test
	public void testDeleteStudentWhenExists() {
		Student toDelete = new Student("1","toDelete");
		when(studentRepository.findById("1")).thenReturn(toDelete);
		schoolController.deleteStudent(toDelete);
		InOrder inOrder = inOrder(studentRepository, studentView);
		inOrder.verify(studentRepository).delete("1");
		inOrder.verify(studentView).studentRemoved(toDelete);
	}
	
	@Test
	public void testDeleteStudentWhenNonExisting(){
		Student toDelete = new Student("1","toDelete");
		when(studentRepository.findById("1")).thenReturn(null);
		schoolController.deleteStudent(toDelete);
		verify(studentView).showError("No existing student with id 1", toDelete);
		verifyNoMoreInteractions(ignoreStubs(studentRepository));
	}

}
