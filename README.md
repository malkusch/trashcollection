# Trashcollection

This is an application which would send a Telegram message on trash collection days.

# Install and Run

Requirements:

- Java-16

## Get the application

You can either build it from the source or download the latest snapshot.

### Build

    mvn package

This will build the executable jar `target/trashday-app.jar`.

### Download

Download the [latest snapshot](https://github.com/malkusch/trashcollection/releases/download/master-snapshot/trashday-app.jar).

## Configuration

First you need a [Telegram bot](https://core.telegram.org/bots). Then start a group with that bot and get the [chatId](https://stackoverflow.com/questions/32423837/telegram-bot-how-to-get-a-group-chat-id).

Create an `application.yml` in the same directory. Then configure the telegram bot and the ical URL for your trash collection calendar in that file. See also [`application-sample.yml`](https://github.com/malkusch/trashcollection/blob/master/application-sample.yml) for an example. 

## Run

    java -jar trashday-app.jar

If you want to use a different configuration file (e.g. `/etc/trashday.yml`), set the command line argument `--config=<file>`. Example:

    java -jar trashday-app.jar --config=/etc/trashday.yml
