package school.repository.mongo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import school.model.Student;
import school.repository.StudentRepository;

public class StudentRepositoryMongo implements StudentRepository {
	
	public static final String STUDENTS_COLLECTION_NAME = "students";
	public static final String SCHOOL_DB_NAME = "school";
	private MongoCollection<Document> studentCollection;

	public StudentRepositoryMongo(MongoClient client) {
		studentCollection = client
				.getDatabase(SCHOOL_DB_NAME)
				.getCollection(STUDENTS_COLLECTION_NAME);
	}

	@Override
	// Dimostrazione, si dovrebbe filtrare o usare POJO
	public List<Student> findAll() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
				.map(this::fromDocumetToStudent)
				.collect(Collectors.toList());
	}

	private Student fromDocumetToStudent(Document d) {
		return new Student(""+d.get("id"), ""+d.get("name"));
	}

	@Override
	public Student findById(String id) {
		Document d = studentCollection.find(Filters.eq("id", id)).first();
		if (d != null)
			return fromDocumetToStudent(d);
		return null;
	}

	@Override
	public void save(Student student) {
		studentCollection.insertOne(
			new Document()
				.append("id", student.getId())
				.append("name", student.getName()));

	}

	@Override
	public void delete(String id) {
		studentCollection.deleteOne(Filters.eq("id",id));
	}

}
