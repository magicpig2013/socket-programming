CS 3251 Project 2

Group Members: Zhe Zhu and Shuangke Li

High Level Explanation: The overall method utilized here was to create a server
that, upon a client connection attempting to be established, would either
allow for a successful connection or return an "ERROR" of some sort to the user
alerting them that they could not connect to the server. This "ERROR" came in the
form of a message alerting the potential new Client connection that the server
is currently full (aka a "server-overload"). If, however, the Server was not full
and the Client could successfully connect to it, then the server would handle
creating a new thread in which that Client's game of Hangman could run. Thus,
ultimately, the game functions using Multithreading, allowing each of the three
concurrent games to run in their own thread within the main process. By doing this
it allows us to more easily not only terminate threads when the game itself has
run its course, but also keep track of the number of current connections via use
of a global variable that all of the threads have access to. When a Client is
successfully connected to the Server, the Client plays its own game of Hangman,
getting 6 incorrect guesses in which they can guess the correct word.

If the server is established with no defined dictionary passed in to the terminal
upon startup, then a default dictionary will be used in order to choose the words for the Clients to guess.
If, however, a dictionary name is entered at the time the Server is started in
the terminal, then that will be the dictionary from which all Client words will
be chosen.

Division of Work:

    Zhe Zhu:
        -- created Server code
        -- Merge Client and sever code together
        -- write makefile
    SHuangke Li:
        -- created Client code
        -- write the readMe file

How To Use:
    Required Dependencies/Packages:
    The program needs to be run under java enviorment.
    So you need to download java JDK before running it.
    You should put all files in the same directory(includes words.txt).

    Code Startup:

        Step1: comple all java files ---- javac Server.java
                                     ---- javac Client.java
                                     ---- javac ServerExtra.java
                                     ---- javac ClientExtra.java

        Step2: running Server file at first:
                                    ---- java Server [port Number] (optional) [dictionary name]


        ***NOTE: if no input word text file, program will use default dictionary
                 Notice the dictionary name should follow the postfix .txt(filename.txt)

            Once the Server is started in its own terminal/command line, you can now
                begin to connect Clients to the Server and play games of Hangman via
                the Clients. In order to do this, open a new terminal/command line
                and change directory into the directory containing the project. Once
                inside the appropriate directory, type the following command to
                begin a Client in this terminal:

                    java Client [IP address] [port Number]

                ***NOTE: the port number should be the same as the one on which the
                    Server is running, or else the Client will not be able to
                    successfully connect to the Server and play the game

            If desired, up to 3 Clients can be connected to the same Server at once
                but if you attempt to connect a fourth Client to the same Server
                (prior to one of those Clients closing down/finishing their games)
                then the Server will respond with a "server-overload" message to the
                Client, causing that connection to be gracefully terminated.

            If, however, you're playing the game of Hangman on one of the Clients
                already connected to the Server, then the following controls apply:
                1) the Server will send a "Ready to start? (y/n)" message to the Client.
                    This message will be displayed on the Client's screen. You must
                    enter either an uppercase or lowercase y or n in order to
                    send a valid response to the Server.
                    ***NOTE: if you attempt to enter a letter (or number) that is not
                        either an upper or lowercase y or n, the Client will prompt the
                        message again to ask you to enter a valid response

                2) if the Client enters a y(indicating that yes they wish to begin the game)
                    then the Server will next access the appropriate dictionary and choose
                    a word for the Client to play a game of Hangman with

                3) the client will then have all of this information displayed to it
                    and will be prompted to enter the letter which they wish to guess.
                        -- if the Client enters more than one character (or a number) then
                            they will be notified that they have done as much and will be
                            asked to input a valid one-character response until such a time
                            as they have successfully submitted a one-character response
                        -- if the Client has already guessed the letter they're attempting
                            to guess, they will be notified and prompted for a new letter
                4) Once the Client has submitted a valid letter, their guess will be sent to
                    the Server where it will be compared against the word for them to guess
                        -- if the letter is contained in the word, then the message sent to
                            the Client will be updated to include this letter in the place
                            of the "_" that was previously there
                        -- if the letter is not contained within the word, then the letter will
                            be added to the list of incorrect guesses made and will be sent to
                            the Client as such
                5) once the server has processed the Client's guess, it will send back a message
                    with the a flag, the word length, and the number of incorrect guesses, as well
                    as a string representing the word and a string representing the list of
                    incorrect guesses
                6) the Client will then break this information down and present it to the user
                    in the format mentioned above
                7) the program will then repeat steps 3-5 until such a time as the Client has
                    made 6 incorrect guesses for the letters contained within the word OR the
                    Client has successfully guessed all of the letters in the word and has,
                    therefore, won the game
                8) at such a time as the Client has guessed incorrectly 6 times or guessed all
                    letters correctly, then the appropriate message will be returned to the user
                    (either "You Win!" or "Game Over")
                9) this message will then be broken down by the Client and displayed to them
                10) once the game has ended (either Client won or lost), the connection to the
                    server will be terminated, freeing up a space for a new connection to be
                    made to the Server (thus starting a new game)

Test Results/Outputs:

    The Default Dictionary used (when one is not provided in the startup of the Server) is the
        following:
            you
            see
            sun
            tree
            wind
            love
            water
            trade
            fever
            struct
            string
            object
            integer
            ethurem
            bitcoin

        It consists of 3 three-letter words, 3 four-letter words, 3 five-letter words, 3 six-letter words and 3 seven-letter words.

****Server side

---case1 using optional dictionary---
$ java Server 2017 words.txt
---case2 using default dictionary
$ java Server 2017

****Client side

--------case1 successfully guess out the word--------------

$ java Client 127.0.0.1 2017
Ready to start game? (y/n):
y
_ _ _ _
Incorrect Guesses:

Letter to guess:
z
_ _ _ z
Incorrect Guesses:

Letter to guess:
a
_ _ _ z
Incorrect Guesses:a

Letter to guess:
b
_ _ _ z
Incorrect Guesses:ab

Letter to guess:
f
_ _ _ z
Incorrect Guesses:abf

Letter to guess:
q
q _ _ z
Incorrect Guesses:abf

Letter to guess:
u
q u _ z
Incorrect Guesses:abf

Letter to guess:
i
q u i z
Incorrect Guesses:abf

You Win!
Game Over!

--------case2 input the used word and invailid symbol---------------------------
Letter to guess:
a
_ _ _ _
Incorrect Guesses:a

Letter to guess:
a
Error! Letter a has been guessed before, please guess another letter.
Letter to guess:

-----case3 user enter the invalid symbol----
Letter to guess:
5
Error! Please guess a letter.
Letter to guess:

-----case3 lose the game----

Ready to start game? (y/n):
y
_ _ _ _
Incorrect Guesses:

Letter to guess:
a
_ _ _ _
Incorrect Guesses:a

Letter to guess:
d
_ _ _ _
Incorrect Guesses:ad

Letter to guess:
g
_ _ _ _
Incorrect Guesses:adg

Letter to guess:
v
_ _ _ _
Incorrect Guesses:adgv

Letter to guess:
j
_ _ _ _
Incorrect Guesses:adgvj

Letter to guess:
g
Error! Letter g has been guessed before, please guess another letter.
Letter to guess:
c
_ _ _ _
Incorrect Guesses:adgvjc

You Lose :(
Game Over!

-----case4 exceed the maximal number(3) of clients----
$ java Client 127.0.0.1 2017
Ready to start game? (y/n):
y
Server overloaded. Try again after one minute.




For Extra part:
Step1: Start the server using
                ---- java ServerExtra [port Number] (optional) [dictionary name]
Step2: Run two clients using
                ---- java ClientExtra [IP address] [port Number]
        Note that if only one clients is running, it will wait doing nothing until the other clients to be his opponent.
Step3: Play the game just as the normal case.

Note: It is a two-player games. There is no option provided for single player since we believe cooperating makes a brighter world. There is no option provided for player to choose whether to start since there is no reason for you to open this game and choose to not play it. The game will start when two players are both connected. When one player make guess, the another will need to wait and he can see the result of his gamemate's guess after the guess is made. The game's rule is the same with the single mode, that is, when these two player have make totally six wrong guess, they lose the game.  

Sample output:
    Client1:
        You are player 1
        Both players is connected
        Game start ^_^
        It's your turn
        Letter to guess:
        a
        _ _ _
        Incorrect Guesses:a

        Waiting for player 2 to guess
        _ _ _
        Incorrect Guesses:ab

        It's your turn
        Letter to guess:
        e
        _ _ _
        Incorrect Guesses:abe

        Waiting for player 2 to guess
        _ _ _
        Incorrect Guesses:abes

        It's your turn
        Letter to guess:
        u
        _ _ u
        Incorrect Guesses:abes

        Waiting for player 2 to guess
        y _ u
        Incorrect Guesses:abes

        It's your turn
        Letter to guess:
        o
        y o u
        Incorrect Guesses:abes

        You Win!
    Client 2:
        You are player 2
        Both players is connected
        Game start ^_^
        Waiting for player 1 to guess
        _ _ _
        Incorrect Guesses:a

        It's your turn
        Letter to guess:
        b
        _ _ _
        Incorrect Guesses:ab

        Waiting for player 1 to guess
        _ _ _
        Incorrect Guesses:abe

        It's your turn
        Letter to guess:
        s
        _ _ _
        Incorrect Guesses:abes

        Waiting for player 1 to guess
        _ _ u
        Incorrect Guesses:abes

        It's your turn
        Letter to guess:
        y
        y _ u
        Incorrect Guesses:abes

        Waiting for player 1 to guess
        y o u
        Incorrect Guesses:abes

        You Win!
