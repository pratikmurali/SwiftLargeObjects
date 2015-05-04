# SwiftLargeObjects
This is a sample Project to demonstrate Large Objects upload to Oracle Cloud Storage Service using the Oracle Cloud Storage Java SDK.

##Background
Oracle Cloud Storage is an Object Store implementation which in simple terms is a BIG distributed hash map. You put objects into a container (you own) using a unique key (which you define) and later retrieve the object using the same key. The main caveat is that objects can be HUGE – upto terabyte of data. The interface to the object store is simple and based on
OpenStack Swift API. Essentially, the same concept as Amazon S3.

##Problem Statement
The primary use-case of object store is back-up and archive which means that clients want to upload large amounts of data. The goal is to optimize the upload and download of these objects by parallelization. Specifically:

  1. The user should have a simple client side API to upload and retrieve large files.
  2. The user must be oblivious to the upload mechanims underpinnings, like chunking of the files into segments, using     
     manifests, retries and resume and such.
  3. The user must have a simple api to retrieve the uploaded large file.

##Non-functional Design Requirements
  1. Data-Integrity
     I ensure data integrity by using an MD5 hash on the file chunks and the manifest. I store the hashes of the file chunks       into a map and store that as additional metadata while storing the Manifest. Additionally the manifest stores information
     about the file chunks and the manifest itself has an MD5 has associated with it which is sent as a part of the HTTP           header. When the file chunks and the manifest are retrieved, the chunks a stiched back, a new MD5 hash is calcluated and
     compared with the existing MD5 hash to verify Data Integrity.
  2. Performance
     Disk I/O is faster than the network uploads. With this in mind, i tried out various combination of chunk sizes and chunk      numbers. I have a 4 core CPU and SSD hard drive. Given that file chunk uploads are homogenous tasks which don't interfere
     with each other, i was able to exploit some parallelism over here. I played around with a FixedPool and a CachedPool     
     executor and got better performance with a fixed pool and controlled number of threads (which means larger chunk sizes 
     and fewer chunks). ThreadPools use a bounded buffer from which upload tasks are executed. 
  3. Fault-Tolerance
     Fault Tolerance is best taken care off by using a specific framwork like AKKA which uses actors and supervised      
     hierarchies. Building that is next on my TODO list. At the moment i am using some basic FT to abort tasks in order
     to ensure data integrity.
  4. Concurrency
     I tried to exploit some parallelism over here by dividing the homogenous tasks of uploading file chunks to be processed       concurrently. I submit a batch of computations (which in this case is an upload) to a ThreadPool executor and retain a
     Future associated with each task in that pool. I then use the completion service to pool for the results of the future.
