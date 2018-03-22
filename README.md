# eta

Extract, Transform, And...

## About

Fairly recently I learned the acronym ETL for Extract, Transform, Load. From context I've gathered, that's when you recognize that the problem of getting your data ready for analysis and use is enough of a problem that you should consider it separately. "Most of our work," say the data scientists I've talked to, "is data janitorial work."

I have a couple-three large collections of text that I've created or squirreled away for several years. Every so often I try and create a project to analyze and remix this text, but now I recognize my ETL was complected with the specifics of the analysis or remixing I had in mind. For this project, I will concentrate on extracting the data to host it myself. I just learned about Clojure seesaw, which wraps Java Swing for vastly increased usability; I will consider doing some very basic visualization as well.

## Usage

Dunno yet.

## Progress

- Access pinboard data
- Access twitter statuses by id
- Transform pinboard data from string representations to Java objects (URIs, dates, etc)
- Specify :pinboard.pin/entity to generously accept errors in data, marking it for later improvement
- Persist pinboard data as flat files (putting off db decisions)
- Run an full extraction job on all pinboard posts

## Next

- I won't have my own storage solution done soon, so it would be nice to have a way to synchronize your data without re-running the full job
- Classify pins: ordinary page vs. saved tweet; saved tweet that's accesible through the API vs. tweets that have since been deleted
- Retrieve tweet data (saved on pinboard)
- Retrieve tweet data (my own tweet archive)
- Retrieve user data
- Play with parsing of org-mode files for personal journal extraction
- Generalize solutions that are common between pinboard and twitter extractions
- Provision and load Datomic (dev, cloud)

## The Data

May include, is not limited to, 

- pinboard.in account
- twitter account 
- tweets by others saved to pinboard.in
- diary files in my homedir
- distance 2 or 3 around me in the Twitter social graph
