elastic-compass
===============

Introduction
------------

This project aims at simplifying the migration of legacy projects from 
[Compass](http://www.compass-project.org/) to [ElasticSearch](http://www.elasticsearch.org/).

Since [Shay Banon](http://www.kimchy.org/) ([@kimchy](https://twitter.com/#!/kimchy))
announced that he does not plan to further develop Compass
(see [The Future of Compass & ElasticSearch](http://www.kimchy.org/the_future_of_compass/))
people are stuck due to ElasticSearch's lack of Compass features like Spring or Hibernate
integration, batch indexing or simply it's Object/Search Engine Mapping (OSEM).

Project State
-------------

Work on this project is currently in progress since it just recently left it's proof of 
concept state. It's planned to support all former Compass JUnit tests and document 
everything that fails (see "Unsupported features").

Unsupported features
--------------------

- transactions
- no query.toString()
- no dotted paths
- alias property
- multiple @SearchableId not supported
- polymorphism not yet supported
- _analyzer property not working as expected
- poly alias query not yet supported
- extended aliases not stored in all property
- class level boosting
- adding custom properties
- analyzers
- escaping of dotted paths (e.g. "'test.me':test)
- more like this
- formatting values
- 'now' in range queries
- reverse
- spell checking
- sub indexes
- sorting by doc id

Migration Guide
---------------

TODO

Links
-----

- [Continuous Integration](https://oss.molindo.at/project.html?projectId=project11)
- [Download](https://oss.sonatype.org/index.html#nexus-search;quick~elastic-compass)
