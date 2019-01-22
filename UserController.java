package abc;
import java.security.SecureRandom;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.aptechnolo.CommonAPIConstants;
import com.aptechnolo.CommonUtils;
import com.aptechnolo.NetworkConstants;
import com.aptechnolo.email.EmailSender;
import com.aptechnolo.model.GeneralResponse;



@RestController    // This means that this class is a Controller
public class UserController {
	@Autowired // This means to get the bean called userRepository
	private UserRepository userRepository;
    @PostMapping(path="/api/users/register")
      public @ResponseBody GeneralResponse addNewUser (@RequestParam String username,
          @RequestParam String password,@RequestParam String fullName,@RequestParam int userType,@RequestParam String mobileNumber) {//Add image Requests 
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        String userAccessToken="colio_access";
        boolean isAlreadyPresent=true;
        try {
          Optional<User> optional=userRepository.findUserByUserName(username);
          User user= optional.get();
          }
        catch(NoSuchElementException e) {
          isAlreadyPresent=false;
        }
        try {
          if(isAlreadyPresent) {
            return new GeneralResponse(NetworkConstants.SUCCESS,"User Already Exists");
          }
        userRepository.save(new User(username,password,userType,fullName,"empty",mobileNumber));
         String token=UUID.randomUUID().toString();
        userAccessToken = userAccessToken+token;
        userRepository.updateAccessToken(username,userAccessToken);
        }catch(Exception e) {
          e.printStackTrace();
          return new GeneralResponse(NetworkConstants.EXCEPTION,"Exception");
        }
        try {
        EmailSender.sendEmail(username,"Activate your Account","http://"+CommonAPIConstants.BASE_URL+"api/enableUserEmail/"+username+"/"+userAccessToken);
        }catch(Exception e) {
          e.printStackTrace();
        }
        return new GeneralResponse(NetworkConstants.CREATED,"User Information Added"+userAccessToken);
      }
      
      
      @GetMapping(path="api/enableUserEmail/{userName}/{authToken}")
      public @ResponseBody GeneralResponse verifyUserEmail(@PathVariable(value="authToken")String authToken,
          @PathVariable(value="userName")String userName) {
        Optional<User> optional=userRepository.findUserByUserName(userName);
        if(optional.get()!=null) {
          User user=optional.get();
          if(authToken.equals(user.getUserAccessToken())) {
            userRepository.updateUserStatus(userName,true);	
          }
          else {
            return new GeneralResponse(NetworkConstants.FAILURE,"Wrong Token");
          }
        }
        else {
          return new GeneralResponse(NetworkConstants.FAILURE,"User Doesn't Exists");
        }
        return new GeneralResponse(NetworkConstants.SUCCESS,"Email Verified");
      }


      @GetMapping(path="/api/test")
      public String testRun() {
        userRepository.updateAccessToken("test@gmail.com","updatedToken");
        //userRepository.userStatusUpdate();
        return "Testing Successful";
      }

      @GetMapping(path="/api/updateDetails")
      public String update() {
        userRepository.updateAccessToken("test@gmail.com","updatedToken");
        return "Successfully Updated";
      }
      
  }
