import { Moment } from 'moment';
import { IImageAttaque } from 'app/shared/model/image-attaque.model';

export const enum Localisation {
    FEUILLES = 'FEUILLES',
    FRUITS = 'FRUITS',
    FLEURES = 'FLEURES',
    TIGE = 'TIGE',
    RACINE = 'RACINE'
}

export interface IAttaque {
    id?: number;
    description?: any;
    flag?: boolean;
    localisation?: Localisation;
    dateValidation?: Moment;
    dateAjout?: Moment;
    imagesAttaque?: string;
    insecteId?: number;
    cultureId?: number;
    attaques?: IImageAttaque[];
    chercheurId?: number;
    typeDegatId?: number;
}

export class Attaque implements IAttaque {
    constructor(
        public id?: number,
        public description?: any,
        public flag?: boolean,
        public localisation?: Localisation,
        public dateValidation?: Moment,
        public dateAjout?: Moment,
        public imagesAttaque?: string,
        public insecteId?: number,
        public cultureId?: number,
        public attaques?: IImageAttaque[],
        public chercheurId?: number,
        public typeDegatId?: number
    ) {
        this.flag = this.flag || false;
    }
}
