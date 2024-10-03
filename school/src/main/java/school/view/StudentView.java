package school.view;

import java.util.List;

import school.model.Student;

public interface StudentView {

	void showAllStudents(List<Student> students);
	void showError(String message, Student student);
	void studentAdded(Student student);
	void studentRemoved(Student student);

}
