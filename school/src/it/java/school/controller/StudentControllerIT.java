package school.controller;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mongodb.MongoClient;

import school.cotroller.SchoolController;
import school.model.Student;
import school.repository.StudentRepository;
import school.repository.mongo.StudentRepositoryMongo;
import school.view.StudentView;

public class StudentControllerIT {
	
	@Mock
	private StudentView studentView;
	
	private StudentRepository studentRepository;
	private SchoolController schoolController;
	
	private AutoCloseable closeable;
	
	// docker run -p 27017:27017 --rm mongo:4.4.3

	@Before
	public void setUp(){
		closeable = MockitoAnnotations.openMocks(this);
		studentRepository = new StudentRepositoryMongo(new MongoClient("localhost"));
		for (Student student : studentRepository.findAll()) {
			studentRepository.delete(student.getId());
		}
		schoolController = new SchoolController(studentRepository, studentView);
	}

	@After
	public void tearDown() throws Exception {
		closeable.close();
	}

	@Test
	public void testAllStudents() {
		Student student = new Student("1", "test");
		studentRepository.save(student);
		schoolController.allStudents();
		verify(studentView).showAllStudents(asList(student));
	}

	@Test
	public void testNewStudentAdded() {
		Student student = new Student("1", "test");
		schoolController.newStudent(student);
		verify(studentView).studentAdded(student);
	}

	
	@Test
	public void testDelete() {
		Student toDelete = new Student("1","toDelete");
		studentRepository.save(toDelete);
		schoolController.deleteStudent(toDelete);
		verify(studentView).studentRemoved(toDelete);
	}
	
}
