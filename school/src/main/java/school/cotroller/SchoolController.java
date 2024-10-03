package school.cotroller;

import school.model.Student;
import school.repository.StudentRepository;
import school.view.StudentView;

public class SchoolController {

	private StudentRepository studentRepository;
	private StudentView studentView;

	public SchoolController(StudentRepository studentRepository, StudentView studentView) {
		this.studentRepository = studentRepository;
		this.studentView = studentView;

	}

	public void allStudents() {
		studentView.showAllStudents(studentRepository.findAll());
	}

	public void newStudent(Student student) {
		Student existingStudent = studentRepository.findById(student.getId());
		if(existingStudent != null) {
			studentView.showError("Existing studend with id " + existingStudent.getId(), existingStudent);
			return;
		}
		studentRepository.save(student);
		studentView.studentAdded(student);
	}

	public void deleteStudent(Student student) {
		
		Student toDelete = studentRepository.findById(student.getId());
		if(toDelete == null) {
			studentView.showError("No existing student with id " + student.getId(), student);
			return;
		}
		
		studentRepository.delete(student.getId());
		studentView.studentRemoved(student);
	}
	
	
}
