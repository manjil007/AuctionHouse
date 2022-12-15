# CS351L Final Project (Auction House)

Yun Zheng, Manjil Pradhan, and Raju Nayak

## How to run  the program

There are 3 programs that make up the overall auction house program,
and they all act differently but work together.

### Bank
The first program that must be run is the bank. Other program will not run 
without running the bank first as it is the backbone of entire programs. 

Bank can be run by executing and running the bank.jar. Use the following to run on either terminal 
or cmd:\
`java -jar bank.jar`\
Make sure you are on the right directory before running the jar file.
The bank GUI will display the bank ip address and the port. Bank GUI is there to make easy for existing the bank.
All other information will print out on the console. If the bank closes the entire set of programs will fail.

### Auction House
The Auction House does not have to be the second program to run,
but the agents will not be able to do much if there is no auction house.

To run the auction house you need to have three input args:\
`java -jar auctionhouse.jar ipAddress port# AuctionItems`\
The `ipAddress` and `port#` will come from the bank console or GUI.

The auction house will automatically count down fom 30 seconds fo any item that
has been bid on. If an item is bid on again the timer will reset. The auction house 
print some information on the console. The auction house, when closed, should not beak the program. 
The bank should just know that the auction house closes and alert agents.

*GUI is only for ease of exiting the program.*

### Agent

To start an agent you have to run the following:\
`java -jar agent.jar ipAddress port#`\
The `ipAddress` and `port#` will come from the bank console or GUI. It is the as for auction house.

The first GUI that appears will ask to first create the bank account. Put the name and total amount
in the box and click submit. After creating account, the auction house will appear with all the bidding 
information. Now agent can start the biding process. Agent GUI will display different auction house as different tabs.
There is back and next button which helps to navigate through different items of auction house.
The slider helps to select the amount for biding. Bid status box display the bidding message.

Like the auction house, if you exit agent GUI it will not break other program.


## Work Distribution

Raju worked on agent part of this project.\
The _**item watcher threads**_ wait for item updates from the auction house
socket and keeps the items up to date on the Agent's end.\
The _**agent thread**_ waits for the bank to send new Auction Houses or updates
to the Agent's bank amount.\
The _**GUI thread**_ updates the GUI, requesting updates from the Agent class.

Manjil contributed to the auction house.
Manjil focused on the threads, coded the logic behind the items. \
The Auction House works by using 4 types of threads\
There is a _**auction house system thread**_ that
makes the auctioneer thread and agent handler thread, then
is always waiting for new agents, and then updates them
before creating a new agent listener thread for each agent.\
The _**salesman thread**_ updates the timer every second or so to make
the countdown work.
The _**client thread**_ constantly is pulling from a blocking queue
then handling the events as needed.
The _**client handler threads**_ are just waiting for bids from agents, and
sending them to the handler for thread safe handling.

Yun worked on the bank program focusing on the sockets and closing the program.\
The Bank operates within two thread groups: \
_**BankManager**_: a single
thread that handles all the ClientMessage objects sent into the LinkedBlockQueue (LBQ)
and creates ClientAccounts upon connection to the BankManager\
_**ClientAccount**_(s): which each serve as its own thread is created upon connection of
an Agent to the BankManager.\
\
For the communication between the Agents, Auction Houses and the Bank
we created Serializable objects to contain the information being sent:
BidRejection, AuctionBidManager, and ClientMessage.
\
_**BidRejection**_ communicates failed bids and connection closures.
_**AuctionBidManager**_ contains the information on an item which is sent to agents.\
_**ClientMessage**_ contains the details of fund transfers to Auction House Bank
Accounts from bidder accounts. All are handled in the Bank Manager object.\

As for closing the program, if the Bank is closed/terminated, the AuctionHouseSystem
object detects this and then sends an "Auction closed." message to all agents connected
to it. If an AuctionHouse is closed, this closure is detected by the Agent which then handles
all necessary changes to the display. If an Agent is disconnected /closed, it is detected by
both the AuctionHouse(s) and ClientAccount(s) which handle the necessary bookkeeping on their
respective ends.

Even though works were distributed among group members, we all helped each others and
worked cooperating each others.

## Bugs and Errors

- The programmed failed to send the bid winning message to all the agents and reset the price for items 
instead of removing from the list.
- The agent GUI display the auction house even after auction house was closed. 
But the console does display the message that auction house was closed.
 

