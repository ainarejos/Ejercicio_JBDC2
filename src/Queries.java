import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;



import java.util.ArrayList;


public class Queries {
	private static final String URL = "jdbc:mysql://194.224.79.42:43306/addressbook?useUnicode=true&useTimezone=true&serverTimezone=UTC&useSSL=false";
	private static final String USERNAME = "alumne";
	private static final String PASSWORD = "tofol";

		private Connection connection; // manages connection
		private PreparedStatement selectAllPeople;
		private PreparedStatement selectPeopleByLastName;
		private PreparedStatement insertNewPerson;
		private PreparedStatement updatePerson;
		private PreparedStatement deletePerson;
		private PreparedStatement getMaxID;

		// constructor
		public Queries() {
			try {
				connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

				// create query that selects all entries in the AddressBook
				selectAllPeople = connection.prepareStatement("SELECT * FROM ADDRESSES");

				// create query that selects entries with a specific last name
				selectPeopleByLastName = connection.prepareStatement("SELECT * FROM ADDRESSES WHERE LastName = ?");

				// create insert that adds a new entry into the database
				insertNewPerson = connection.prepareStatement(
						"INSERT INTO ADDRESSES " + "(FirstName, LastName, Email, PhoneNumber, AddressID) " + "VALUES (?, ?, ?, ?, ?)");

				// create statement that updates entry in database
				updatePerson = connection.prepareStatement(
						"UPDATE ADDRESSES SET FirstName = ?, LastName = ?, Email = ?, PhoneNumber = ? WHERE AddressID = ?");
				
				// create statement that deletes entry from database
				deletePerson = connection.prepareStatement(
						"DELETE FROM ADDRESSES WHERE AddressID = ?");
				
				// create statement that get the last id from clients
				getMaxID = connection.prepareStatement(
						"Select MAX(AddressID) as AddressID FROM ADDRESSES");
				
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				System.exit(1);
			}
		} // end PersonQueries constructor

		// select all of the addresses in the database
		public List<Persona> getAllPeople() {
			List<Persona> results = null;
			ResultSet resultSet = null;

			try {
				// executeQuery returns ResultSet containing matching entries
				resultSet = selectAllPeople.executeQuery();
				results = new ArrayList<Persona>();

				while (resultSet.next()) {
					results.add(new Persona(resultSet.getInt("addressID"), resultSet.getString("firstName"),
							resultSet.getString("lastName"), resultSet.getString("email"),
							resultSet.getString("phoneNumber")));
				}
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			} finally {
				try {
					resultSet.close();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
					close();
				}
			}

			return results;
		}

		// select person by last name
		public List<Persona> getPeopleByLastName(String name) {
			List<Persona> results = null;
			ResultSet resultSet = null;

			try {
				selectPeopleByLastName.setString(1, name); // specify last name

				// executeQuery returns ResultSet containing matching entries
				resultSet = selectPeopleByLastName.executeQuery();

				results = new ArrayList<Persona>();

				while (resultSet.next()) {
					results.add(new Persona(resultSet.getInt("addressID"), resultSet.getString("firstName"),
							resultSet.getString("lastName"), resultSet.getString("email"),
							resultSet.getString("phoneNumber")));
				}
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			} finally {
				try {
					resultSet.close();
				} catch (SQLException sqlException) {
					sqlException.printStackTrace();
					close();
				}
			}

			return results;
		}

		// add an entry
		public int addPerson(String fname, String lname, String email, String num, int id) {
			int result = 0;

			// set parameters, then execute insertNewPerson
			try {
				insertNewPerson.setString(1, fname);
				insertNewPerson.setString(2, lname);
				insertNewPerson.setString(3, email);
				insertNewPerson.setString(4, num);
				insertNewPerson.setInt(5, id);

				// insert the new entry; returns # of rows updated
				result = insertNewPerson.executeUpdate();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				close();
			}

			return result;
		}

		// Update data of a person.
		public int updatePerson(String fname, String lname, String email, String num, int id) {
			int result = 0;

			// set parameters, then execute updatePerson
			try {
				updatePerson.setString(1, fname);
				updatePerson.setString(2, lname);
				updatePerson.setString(3, email);
				updatePerson.setString(4, num);
				updatePerson.setInt(5, id);

				// update entry; returns # of rows updated
				result = updatePerson.executeUpdate();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				close();
			}

			return result;
		}

		// Delete a person.
		public int deletePerson(int id) {
			int result = 0;

			// delete the person.
			try {
				deletePerson.setInt(1, id);

				result = deletePerson.executeUpdate();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				close();
			}

			return result;
		}

		// Get the current last id of the persons.
		public int getMaxID() {
			ResultSet result=null;
			try {

				result = getMaxID.executeQuery();
				if (result.next()) return (1 + result.getInt("AddressID"));
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
				close();
			}
			return 1;
		} 

		// close the database connection
		public void close() {
			try {
				connection.close();
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			}
		}
}
