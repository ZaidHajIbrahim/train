Group I CORRECT FOR DEMO OF LAB 1:
Group: 32 **DONE**
Group: 30 **DONE**
Group: 29 + extra poängen

=======
VIA ZOOM
GROUP: 18 ## They have a vague idea on whats going on, especially chris, kristin did most of the talking. 
Group: 23 ## Basic solution, no issues **DONE**
Group: 14 ## check theier code, the max speed was 18 or 19 **DONE**
Group: 36 ## max speed said was 22. **DONE**
Group: 51 ## simple and clear,
Group: 60 ## i did the simu, works fine, a bit unclear when it came to section handler.
Group: 63 ## group used only 4 semaphores, **DONE**
Group: 50 ## no semaphores, check the code before rejection **DONE** NO REJECTION
	
group: 3  ## didnt hsow up **DONE**
Group: 32 ## well done clear **DONE**
Group: 20 ## reject. **DONE** 
Group: 56 ## clear nad concise **DONE**
Group: 13 ## clear **DONE**

Group: 3 ## **SECOND ATTEMPT** ** CHECK FOR CODE**
 
============

The solutions are by John J. Camilleri.

Trainspotting
=============

Here follows some demo sessions guidelines from Raúl Pardo Jiménez. It
also includes the most common mistake that students tend to have in
this lab.

Initial inspection
------------------

First I ask them to start a simulation with speeds 15 and 5. It is
because with these speeds you can see in a few iterations if the train
can overtake in the middle section. Another good rule of thumb is that
if there are less than four sensors in the middle sensor it is not
always possible to overtake. Also you will see if the trains have
enough time to break before the switched.

**Requirements checked**:

* Good train flow
* Maximum train speed
* No map modifications

After this I check `Lab1.java` to check that they have a `Train` class
that implements `Runnable` (or extends from `Thread`) and that they start
two threads of that class.

**Requirements checked**:

* Two trains - one implementation
* Two trains - two threads

Further questions
-----------------

Here is the list of questions that I ask and the points I try to
evaluate with each question. I normally ask the first 2 to one student
and the other 2 to the other student. But it is common that question 2
involves a discussion with both students.

### How many semaphores do you have in your code? ###

Checks that the amount of semaphores is in the specified range
(i.e. max 10 sempahores). The minimum amount of semaphores is 6, so if
the solution has less than 6, probably they are not taking care of
some critical section. Also look it is important to check that the
initialisation of the semaphores is correct.

**Requirements checked**:

* No excessive semaphores
* Use binary semaphores

### Which are the critical sections in the map and which semaphores are used for each of them? ###

There are 3 critical sections which require one semaphore each. These
are the crossing in the upper station, the right curve and the left
curve. In these critical sections if there is a train already inside
the train trying to get in must break until the other train releases
the critical section. Normally here I ask them to show me, for
instance, how the take the critical section in the crossing.

For the parts of the track when the train choose between two tracks,
i.e. entering the upper or bottom station or the middle section, they
can have from 3 to 6 critical sections. There must always be a default
track (Requirement "Dynamic behaviour"). Therefore, when a train
approaches one of this critical sections there are two approaches:

(i) There is a semaphore for the default track, for instance, the
upper one in the middle section, which the train always tries to
acquire and if it is taken it goes through the alternative one, or

(ii) there are two semaphores: one for the default track and another
for the alternative one, as before the train should first try the
default track first.

In both cases (but especially in (i)) it is important to check that
they release the semaphore properly, it is a common mistake in
approach (i) to always release the semaphore, even if the train took
the alternative path.

**Requirements checked**:

* Good train flow
* Dynamic behaviour
* Trains mind their own business
* Use binary semaphores
* No randomisation

### How did you implement when the train must choose a track in the middle section and in the stations? ###

When choosing a track in the tracks with two options is important to
check that they implemented it using `tryAcquire`. It is a common
mistake to use first `availablePermits` and afterwards `acquire`, but
it can lead to a race condition in the middle track if two trains try
to enter the track from opposite directions.

Specifically the interleaving is as follows:

One train executes `availablePermits` and get 1, then the other train
also executes `availablePermits` and also gets 1 (because the previous
train didn't execute `acquire`) and afterwards both go to the default
track of the middle section.

**Requirements checked**:

* No randomisation
* Good train flow
* Dynamic behaviour

### How did you implement when the train arrives at the station? ###

They should have put a sensor at the station and then use the formula
for the train to wait, and afterwards set the speed to -speed.

**Requirements checked**:

* Waiting at stations

Common reasons for rejection (in Fire)
--------------------------------------

  * Using a switched-based protocol instead of semaphores to protect trains.
    Most such solutions do not meet the criteria that there must be a default path.
  * Not keeping track of semaphore ownership and thereby always releasing "all" semaphores after
    leaving a station or the middle.

Trainmonitoring
===============

Common reasons for rejection:

  * Not checking waiting conditions after wakeups (consider e.g. spurious
    wakeups)
  * Using one lock for all critical sections (introduces unnecessary
    synchronization between parts of the track that are independent in reality)
  * Insufficient synchronization, e.g. accessing a lock-guarded variable
    without taking the lock
