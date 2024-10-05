package school.repository.mongo;

import static org.assertj.core.api.Assertions.*;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import school.model.Student;
import school.repository.StudentRepository;
import static school.repository.mongo.StudentRepositoryMongo.SCHOOL_DB_NAME;
import static school.repository.mongo.StudentRepositoryMongo.STUDENTS_COLLECTION_NAME;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StudentRepositoryMongoTestContainersTest {

	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");

	private MongoClient client;
	private StudentRepository studentRepository;
	private MongoCollection<Document> studentCollection;

	@Before
	public void setUp() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		studentRepository = new StudentRepositoryMongo(client);
		MongoDatabase database = client.getDatabase(SCHOOL_DB_NAME);
		database.drop();
		studentCollection = database.getCollection(STUDENTS_COLLECTION_NAME);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@Test
	public void testFindAll() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");
		assertThat(studentRepository.findAll()).containsExactly(new Student("1", "test1"), new Student("2", "test2"));
	}

	@Test
	public void testFindByIdFound() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");
		assertThat(studentRepository.findById("2")).isEqualTo(new Student("2", "test2"));
	}

	@Test
	public void testSave() {
		Student student = new Student("1", "added");
		studentRepository.save(student);
		assertThat(readAllStudentsfromDatabase()).containsExactly(student);
	}

	@Test
	public void testDelete() {
		addTestStudentToDatabase("1", "toDelete");
		studentRepository.delete("1");
		assertThat(readAllStudentsfromDatabase()).isEmpty();
	}

	private List<Student> readAllStudentsfromDatabase() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
				.map(d -> new Student("" + d.get("id"), "" + d.get("name"))).collect(Collectors.toList());
	}

	private void addTestStudentToDatabase(String id, String name) {
		studentCollection.insertOne(new Document().append("id", id).append("name", name));
	}

}