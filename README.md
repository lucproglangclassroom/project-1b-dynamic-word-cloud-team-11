[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=15981892)
# topwords-scala

running the code WITH file:

sbt stage
 
./target/universal/stage/bin/topwords -c -l -w -s -f -i src/main/scala/hellotest/text/<filename>

-c, -l, -w, -s, -f should all be run with int agruments after the letter

keep -i for file reading

running WITHOUT file:

./target/universal/stage/bin/topwords -c -l -w -s -f

-c, -l, -w, -s, -f should all be run with int agruments after the letter
