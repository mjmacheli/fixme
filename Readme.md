You need to implement simulation tools for the financial markets that exchange a simplified version of FIX messages.

The tools will be able to communicate over a network
using the TCP protocol. The focus in this project are not the trading algos (you can experiment with them after the project is finished), but the implementation of a robust and performant messaging platform.

# V.1
## Router
The router is the central component of your applications. All other components connect to it in order to send messages to other components. The router will perform no buiness logic, it will just dispatch messages to the destination component(s). The router must accept incomming connections from multiple brokers and markets. We call the router a market connetivity provider, because it allows brokers to send messages (in FIX format) to markets, without depending on specific implementation of the market. The router will listen on 2 ports:
- Port 5000 for messages from Broker components. When a Broker establishes the
connection the Router asigns it a unique 6 digit ID and communicates the ID to
the Broker.
- Port 5001 for messages from Market components. When a Market establishes the
connection the Router asigns it a unique 6 digit ID and communicates the ID to
the Market.
Brokers and Markets will include the assigned ID in all messages for identification
and the Router will use the ID to create the routing table.
Once the Router receives a message it will perform 3 steps:
- Validate the message based on the checkshum.
- Identify the destination in the routing table.
- Forward the message.

# V.2
## Broker
The Broker will send two types of messages: 6 Fixme
Hard network programming
- Buy. - An order where the broker wants to buy an instrument
- Sell. - An order where the broker want to sell an instrument
and will receive from the market messages of the following types:
- Exeuted - when the order was accepted by the market and the action succeeded
- Rejected - when the order could not be met
# V.3
## Market
A market has a list of instruments that can be traded. When orders are received from brokers the market tries to execute it. If the execution is successfull, it updates the internal instrument list and sends the broker an Executed message. If the order can’t be
met, the market sends a Rejected message.
The rules by which a market executes orders can be complex and you can play with
them. This is why you build the simulator. Some simple rules that you need to respect
is that an order can’t be executed if the instrument is not traded on the market or if the
demanded quantity is not available (in case of Buy orders).
# V.4
## FIX Messages
All messages will respect the FIX notation.
All messages will start with the ID asigned by the router and will be ended by the checksum.

Buy and Sell messages will have the following mandatory fields:
- Instrument
- Quantity
- Market
- Price
