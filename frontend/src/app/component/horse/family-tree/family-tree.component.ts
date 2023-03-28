import {Component, Input, OnInit} from '@angular/core';
import {Horse} from '../../../dto/horse';
import {Sex} from '../../../dto/sex';
import {ActivatedRoute, Router} from '@angular/router';
import {HorseService} from '../../../service/horse.service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-family-tree',
  templateUrl: './family-tree.component.html',
  styleUrls: ['./family-tree.component.scss']
})
export class FamilyTreeComponent implements OnInit {
  @Input() generations = 5;
  @Input() rootHorse: Horse = {
    father: undefined, mother: undefined, name: '', dateOfBirth: new Date(), sex: Sex.female
  };
  @Input() rootComponent: FamilyTreeComponent | undefined;
  @Input() isRootHorse = true;
  @Input() horse: Horse = {
    father: undefined, mother: undefined, name: '', dateOfBirth: new Date(), sex: Sex.female
  };
  @Input() activeIds: string[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: HorseService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    if (this.isRootHorse) {
      this.rootHorse.id = this.route.snapshot.paramMap.get('id') as unknown as number;
      this.generations = this.route.snapshot.queryParams.generations;
      this.route.queryParams.subscribe(() => {
        if (this.generations == null || this.generations < 1) {
          this.notification.error('Generations can not be set to Zero. Generations will be set to 1.');
          this.generations = 1;
        }
        this.rootComponent = this;
        this.searchHorses();
      });
    }
  }

  expandRec(horse: Horse) {
    if (horse.mother || horse.father) {this.activeIds.push('panel-' + horse.id);}
    if (horse.mother) {this.expandRec(horse.mother);}
    if (horse.father) {this.expandRec(horse.father);}
  }

  searchHorses() {
    if(this.rootHorse.id){
      if(this.generations < 1){
        this.notification.error('Generations can not be set to Zero. Generations will be set to 1.');
        this.generations = 1;
      }
      this.service.getByIdLimited(this.rootHorse.id, this.generations).subscribe({
        next: data => {
          console.log('Familytree: ' + data);
          this.rootHorse = data;
          this.horse = data;
          this.router.navigate([], {queryParams: {generations: this.generations}});
          this.expandRec(this.rootHorse);
        },
        error: error => {
          console.error('error: ', error);
          alert('Could not query horses right now, redirecting to horses');
          this.router.navigate(['/horses']);
        }
      });
    }
  }

  public delete(id: number){
    const observable = this.service.delete(id);
    observable.subscribe({
      next: data => {
        this.notification.success(`Horse was deleted successfully`);
        this.router.navigate(['/horses']);
        this.ngOnInit();
      },
      error: error => {
        console.error('Error deleting horse', error);
        this.notification.error('Could not delete this horse. Errorcode: ' + error.status + ', Errortext: ' + error.error.errors);
      }
    });
  }
  isFemale(sex: Sex): boolean{
    return sex === Sex.female;
  }

  removeActiveIds() {
    this.activeIds = [];
  }

}
