class CitadelException(Exception):

    def __int__(self, msg):
        self.msg = msg
