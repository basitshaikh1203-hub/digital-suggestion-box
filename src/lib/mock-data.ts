export type FeedbackType = 'Suggestion' | 'Complaint' | 'Problem';
export type SuggestionStatus = 'Received' | 'Under review' | 'In progress' | 'Resolved';

export type SuggestionRecord = {
  id: string;
  referenceCode: string;
  type: FeedbackType;
  category: string;
  location: string;
  description: string;
  status: SuggestionStatus;
  votes: number;
  response?: string;
  images?: {
    id: string;
    name: string;
    url: string;
  }[];
  createdAt: string;
};
export const categories = ['Academics', 'Campus & facilities','Connectivity', 'Library', 'Canteen', 'Other'];
export const locations = ['Corridor','Campus','Classroom', 'Library', 'Workshop', 'Canteen', 'Lab', 'Turf', 'Online / not location-specific'];
export const rooms = ['Room 101', 'Room 203', 'Room 304', 'Room 405'];
export const floors = ['Ground floor', '1st floor', '2nd floor', '3rd floor', '4th floor'];

export const initialSuggestions: SuggestionRecord[] = [
  {
    id: 'sug-library-hours',
    referenceCode: 'SB-K4R8M2',
    type: 'Suggestion',
    category: 'Library',
    location: 'Library',
    description: 'Could the reading room stay open for a little longer during internal assessment weeks? A quieter evening window would help many of us prepare.',
    status: 'Under review',
    votes: 18,
    response: 'The library team is reviewing a trial extension for assessment weeks.',
    createdAt: '2025-02-18',
  },
  {
    id: 'sug-water',
    referenceCode: 'SB-T9W3Q6',
    type: 'Problem',
    category: 'Campus & facilities',
    location: 'Main building',
    description: 'The drinking water station near the first-floor staircase needs a filter check. The water has had an unusual taste since last week.',
    status: 'In progress',
    votes: 27,
    response: 'A maintenance request has been raised with the facilities team.',
    createdAt: '2025-02-14',
  },
  {
    id: 'sug-shuttle',
    referenceCode: 'SB-P7D5H8',
    type: 'Suggestion',
    category: 'Transport',
    location: 'Online / not location-specific',
    description: 'A simple notice of the afternoon shuttle departure time would make it easier to plan the trip back after practical sessions.',
    status: 'Received',
    votes: 11,
    createdAt: '2025-02-11',
  },
  {
    id: 'sug-canteen',
    referenceCode: 'SB-V2N6C4',
    type: 'Complaint',
    category: 'Canteen',
    location: 'Canteen',
    description: 'Please consider adding one affordable, filling option to the menu each day for students staying late on campus.',
    status: 'Resolved',
    votes: 34,
    response: 'A daily value meal is now being trialled at the canteen counter.',
    createdAt: '2025-01-28',
  },
];