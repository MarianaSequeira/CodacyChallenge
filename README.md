# CodacyChallenge

Spring Boot application of the Backend Developer Exercise implementation. 
The current implementation allows testing the functionalities developed through the terminal or through the use of the developed API. The terminal will always ask for the Web URL associated to the repository in analysis, and depending of the input will perform the selected operation. 

<br>

## Run 

After performing a clone of this repository, to test the application just run the command:

        $ mvn spring-boot:run
        
If you don't have maven installed, you can read the instructions to install it [here](https://maven.apache.org/install.html).

<br> 

## API 

As mentioned above, the system can also be tested using the API available at /codacyChallenge. 
This API has the follow paths:

   **Clone Repository**:
   
This method allows to clone a repository by specifing the owner name and the repository name. 
Currently the system only supports public repositories.
        
        $ GET codacyChallenge/api/repos/{owner}/{repo}/clone

<br>

   **Get Branches List**:
   
This method allows to retrieve the list of branches from `owner`'s repository named `repo` 
        
        $ GET codacyChallenge/api/repos/{owner}/{repo}/branches

<br>

   **Get Branches List using GitHub API**:
   
This method allows to retrieve the list of branches from `owner`'s repository named `repo` , using the GitHub API. If an error occurs during the request to the GitHub API, the system automatically redirects the request to the last enumerated PATH (fallback).

        $ GET codacyChallenge/gitApi/repos/{owner}/{repo}/branches

<br>

   **Get All Commit List**:
        
This method allows to retrieve the list of commits from `owner`'s repository named `repo` , using the GitHub API. If you want you can specify the branch to list the commits from that specific branch. However, the `branchName` paremeter is not required, and the default behaviour is to return the commits from all the branches. 
 
        $ GET codacyChallenge/api/repos/{owner}/{repo}/allCommits?sha={branchName}

<br>


   **Get All Commit List using GitHub API**:
   
This method allows to retrieve the list of commits from `owner`'s repository named `repo`, using the GitHub API. If an error occurs during the request to the GitHub API, the system automatically redirects the request to the last enumerated PATH (fallback). If you want you can specify the branch to list the commits from that specific branch. However, the `branchName` paremeter is not required, and the default behaviour is to return the commits from all the branches. 
        
        $ GET codacyChallenge/gitApi/repos/{owner}/{repo}/allCommits?sha={branchName}    
   
<br>

   **Get Commit List by Page**:
   
This method allows to retrieve the list of commits by Page from `owner`'s repository named `repo`, using the GitHub API. The page and per_page parameters must be a positive integer.

        $ GET codacyChallenge/api/repos/{owner}/{repo}/commits?page={page}&per_page={per_page}  

<br>

   **Get All Commit List by Page using GitHub API**:
        
This method allows to retrieve the list of commits by Page from `owner`'s repository named `repo`, using the GitHub API. The page and per_page parameters must be a positive integer.
         
        $ GET codacyChallenge/gitApi/repos/{owner}/{repo}/commits?page={page}&per_page={per_page}  

