- First, import Lucene library (version 7.7.2) to the project
- Remove all files in the (files/docs), (files/index) and (files/norm-docs) directories
- Copy or clone your target Android project in (files/docs)
- Run Manager.java class. It receives three lines as inputs (the review text, comma-separated high level categories and comma-separated low level categories)
- It automatically preprocesses the Android source code files and produces clean files in (files/norm-docs)
- Then it indexes the outputs from previous step and creates some .cfe and .cfs files in (files/index)
- Finally, it preprocesses the review and does a search using Lucene API

## Please clone the project and look at the attached pictures (in Github, the pictures are shown in half)