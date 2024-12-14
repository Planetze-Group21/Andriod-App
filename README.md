DEMO VIDEO: [App Demo](https://drive.google.com/file/d/1YCb02uVjufa6tfFR_uC1uAzMdcZlODtH/view?usp=sharing)

Important requirements for .gitignore and .idea:
- The .gitignore file works globally within the repository, so it's not necessary for each collaborator to add their own .DS_Store exclusion manually.
- Similarly, any .idea files you decide to ignore will also apply to everyone, regardless of the OS.
- Make sure everyone has configured their Git correctly for cross-platform work, particularly regarding line endings (e.g., CRLF vs LF). Including this in your .gitattributes file helps

Note on emission calculations: 
- An assumption was made for automobile emission calculation; the rate of KG/KM of CO2E for when the user is unsure of their type of car is estimated to be 0.2 KG/KM 

Model View Presenter Refactoring of Login Module: 
- Model: LoginModel.java
- View: LoginView.java & LoginActivity.java (LoginView is an interface implemented by LoginActivity). 
- Presenter: LoginPresenter.java (also uses UserRepository.java to handle setup questionnaire completion logic)  
