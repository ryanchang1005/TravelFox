import random
import time
import numpy

from django.db import models

# Modeled after base64 web-safe chars, but ordered by ASCII.
ID_CHARS = ('0123456789-'
            'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
            '_abcdefghijklmnopqrstuvwxyz')


class AutoPubIDField(models.CharField):
    description = 'Auto public ID field'
    empty_strings_allowed = False

    def __init__(self, *args, **kwargs):
        kwargs['blank'] = True
        kwargs['unique'] = True
        kwargs['editable'] = False
        kwargs['max_length'] = 20
        kwargs['db_index'] = True
        # Timestamp of last push, used to prevent local collisions
        # if you push twice in one ms.
        self.last_push_time = 0
        # Generating 72-bits of randomness which get turned into 12
        # characters and appended to the timestamp to prevent
        # collisions with other clients.  We store the last characters
        # we generated because in the event of a collision, we'll use
        # those same characters except "incremented" by one.
        self.last_rand_chars = numpy.empty(12, dtype=int)

        super().__init__(*args, **kwargs)

    def get_internal_type(self):
        return "CharField"

    def pre_save(self, model_instance, add):
        if add:
            value = self.create_pushid()
            setattr(model_instance, self.attname, value)
            return value
        else:
            return super().pre_save(model_instance, add)

    def db_type(self, connection):
        return 'char(20)'

    def create_pushid(self):
        # Implement a sortable, shorter is better, unpredictable, universal unique ID
        # currently use Firebase push algorithm:
        # https://firebase.googleblog.com/2015/02/the-2120-ways-to-ensure-unique_68.html
        now = int(time.time() * 1000)
        duplicate_time = (now == self.last_push_time)
        self.last_push_time = now
        timestamp_chars = numpy.empty(8, dtype=str)

        for i in range(7, -1, -1):
            timestamp_chars[i] = ID_CHARS[now % 64]
            now = int(now / 64)

        if now != 0:
            raise ValueError('We should have converted the entire timestamp.')

        uid = ''.join(timestamp_chars)

        if not duplicate_time:
            for i in range(12):
                self.last_rand_chars[i] = int(random.random() * 64)
        else:
            # If the timestamp hasn't changed since last push, use the
            # same random number, except incremented by 1.
            for i in range(11, -1, -1):
                if self.last_rand_chars[i] == 63:
                    self.last_rand_chars[i] = 0
                else:
                    break
            self.last_rand_chars[i] += 1

        for i in range(12):
            uid += ID_CHARS[self.last_rand_chars[i]]

        if len(uid) != 20:
            raise ValueError('Length should be 20.')
        return uid