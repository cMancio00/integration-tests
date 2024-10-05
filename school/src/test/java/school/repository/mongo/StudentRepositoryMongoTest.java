package school.repository.mongo;

import static school.repository.mongo.StudentRepositoryMongo.SCHOOL_DB_NAME;
import static school.repository.mongo.StudentRepositoryMongo.STUDENTS_COLLECTION_NAME;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import school.model.Student;

import org.bson.Document;

public class StudentRepositoryMongoTest {

	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	private MongoClient client;
	private StudentRepositoryMongo studentRepository;
	private MongoCollection<Document> studentCollection;

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}
	
	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}
	
	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(serverAddress));
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
	public void testFindAllWhenDatabaseIsEmpy(){
		assertThat(studentRepository.findAll()).isEmpty();
	}
	
	@Test
	public void testFindAllWhenDatabaseIsNotEmpty(){
		addTestStudentToDatabase("1","test1");
		addTestStudentToDatabase("2", "test2");
		assertThat(studentRepository.findAll())
			.containsExactly(
				new Student("1","test1"),
				new Student("2", "test2"));
	}
	
	@Test
	public void testFindByIdNotFound(){
		assertThat(studentRepository.findById("1")).isNull();
	}
	
	@Test
	public void testFindByIdFound() {
		addTestStudentToDatabase("1", "test1");
		addTestStudentToDatabase("2", "test2");
		assertThat(studentRepository.findById("2")).isEqualTo(new Student("2","test2"));
	}
	
	@Test
	public void testSave(){
		Student student = new Student("1", "added");
		studentRepository.save(student);
		assertThat(
				readAllStudentsfromDatabase()
				).containsExactly(student);
	}
	
	@Test
	public void testDelete(){
		addTestStudentToDatabase("1", "toDelete");
		studentRepository.delete("1");
		assertThat(readAllStudentsfromDatabase()).isEmpty();
	}

	private List<Student> readAllStudentsfromDatabase(){
		return StreamSupport.stream(studentCollection.find().spliterator(), false)
		.map(d -> new Student(""+d.get("id"), ""+d.get("name")))
		.collect(Collectors.toList());
	}

	private void addTestStudentToDatabase(String id, String name) {
		studentCollection.insertOne(new Document()
				.append("id", id)
				.append("name", name));
	}
	

}
