[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=15981892)
# topwords-scala

running the code WITH file:

sbt stage
 
example run: 

./target/universal/stage/bin/topwords -c 10 -l 4 -w 6 -s 2 -f 3 -i /workspace/project-1b-dynamic-word-cloud-team-11/src/main/scala/hellotest/text/lesmiserables01unkngoog_djvu.txt -b src/main/scala/hellotest/blacklist/blacklist.txt

-c, -l, -w, -s, -f should all be run with int agruments after the letter

keep -i for file reading

-b for blacklist reading

running WITHOUT file:

./target/universal/stage/bin/topwords -c -l -w -s -f

-c, -l, -w, -s, -f should all be run with int agruments after the letter

Word Processor Tests:
Test 1: Verifies that the word processor correctly tracks word counts and handles the sliding window logic.
Test 2: Verifies that the word processor respects the minimum frequency requirement.

The tests for this project are written using ScalaTest. You can run them with the following command:

sbt test

This will compile the code, run the test cases defined in the TestWordProcessor.scala file, and output the results.

You can generate a test coverage report using sbt-scoverage. Run the following command:
sbt clean coverage test coverageReport



Extra Credit completed:

Ignore List (blacklist)
Case Insensitive
Every k steps
Minimum frequency