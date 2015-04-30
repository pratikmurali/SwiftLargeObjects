# SwiftLargeObjects
This is a sample Project to demonstrate Large Objects upload to Oracle Cloud Storage Service using the Oracle Cloud Storage Java SDK.

##Background
Oracle Cloud Storage is an Object Store implementation which in simple terms is a BIG distributed hash map. You put objects into a container (you own) using a unique key (which you define) and later retrieve the object using the same key. The main caveat is that objects can be HUGE – upto terabyte of data. The interface to the object store is simple and based on
OpenStack Swift API. Essentially, the same concept as Amazon S3.

##Problem Statement
The primary use-case of object store is back-up and archive which means that clients want to upload large amounts of data. The goal is to optimize the upload and download of these objects by parallelization. Specifically
1. The user should have a simple client side API to upload and retrieve large files.
2. The user must be oblivious to the upload mechanims underpinnings, like chunking of the files into segments, using manifests, retries and resume and such.
3. The user must have a simple api to retrieve the uploaded large file.

##Non-functional Design Requirements
1. Data-Integrity
2. Performance
3. Fault-Tolerance
4. Concurrency
