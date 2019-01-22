import java.util.Optional;

@EnableTransactionManagement
@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
	
	@Query("SELECT p from User p where LOWER(p.userName) = LOWER(:userName)")
	public Optional<User> findUserByUserName(@Param("userName")String userName);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("UPDATE User p SET p.userAccessToken = ?2 where p.userName = ?1")
	public int updateAccessToken(String userName,String userAccessToken);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("UPDATE User p SET p.isUserEnabled = ?2 where p.userName = ?1")
	public int updateUserStatus(String userName,boolean isUserEnabled);
	
}
