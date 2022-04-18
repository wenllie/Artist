package suyat.it32s1.finalproject.Controller;

import suyat.it32s1.finalproject.Model.User;
import suyat.it32s1.finalproject.View.ILoginView;
public class LoginController implements ILoginController {
   ILoginView loginView;
   public LoginController(ILoginView loginView) {
      this.loginView = loginView;
   }
   @Override
   public void OnLogin(String email, String password) {
      User user = new User(email,password);
      int loginCode = user.isValid();
      if(loginCode == 0)
      {
         loginView.LoginError("Please enter Email");
      }else if (loginCode == 1){
         loginView.LoginError("Please enter A valid Email");
      } else if (loginCode == 2)
      {
         loginView.LoginError("Please enter Password");
      }else if(loginCode == 3){
         loginView.LoginError("Please enter Password greater the 6 char");
      }
      else {
         loginView.LoginSuccess("login Successful");
      }
   }
}