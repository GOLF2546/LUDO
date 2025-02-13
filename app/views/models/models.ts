export enum PawnState {
    Start = "Start",
    Normal = "Normal",
    Finish = "Finish",
    End = "End"
  }
  
  export enum Color {
    Red = "Red",
    Blue = "Blue",
    Green = "Green",
    Yellow = "Yellow"
  }
  
  export interface Pawn {
    id: number;
    x: number;
    y: number;
    color: Color;
    state: PawnState;
  }
  
  export interface Player {
    id: number;
    color: Color;
    pawns: Pawn[];
  }
  